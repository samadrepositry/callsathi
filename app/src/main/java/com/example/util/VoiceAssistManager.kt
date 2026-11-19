package com.example.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class VoiceAssistManager(context: Context) {
    private val appContext = context.applicationContext
    private var speechRecognizer: SpeechRecognizer? = null
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    private val _partialText = MutableStateFlow("")
    val partialText: StateFlow<String> = _partialText.asStateFlow()

    private val _rmsLevel = MutableStateFlow(0f)
    val rmsLevel: StateFlow<Float> = _rmsLevel.asStateFlow()

    private val _isSpeakingTts = MutableStateFlow(false)
    val isSpeakingTts: StateFlow<Boolean> = _isSpeakingTts.asStateFlow()

    init {
        tts = TextToSpeech(appContext) { status ->
            ttsReady = status == TextToSpeech.SUCCESS
            if (ttsReady) {
                tts?.language = Locale.US
            }
        }
    }

    fun isSpeechRecognitionAvailable(): Boolean =
        SpeechRecognizer.isRecognitionAvailable(appContext)

    fun startListening(
        localeTag: String = "en-US",
        onFinalResult: (String) -> Unit,
        onErrorMessage: (String) -> Unit
    ) {
        stopListening()
        if (!isSpeechRecognitionAvailable()) {
            onErrorMessage("اس ڈیوائس پر اسپیکر ریکگنیشن سروس دستیاب نہیں۔ نیچے سیمپل بٹن یا ٹائپنگ استعمال کریں۔")
            return
        }
        try {
            val recognizer = SpeechRecognizer.createSpeechRecognizer(appContext)
            speechRecognizer = recognizer
            recognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    _partialText.value = ""
                }

                override fun onBeginningOfSpeech() {}

                override fun onRmsChanged(rmsdB: Float) {
                    val normalized = ((rmsdB + 2f) / 12f).coerceIn(0.05f, 1f)
                    _rmsLevel.value = normalized
                }

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {
                    _rmsLevel.value = 0f
                }

                override fun onError(error: Int) {
                    _rmsLevel.value = 0f
                    val msg = when (error) {
                        SpeechRecognizer.ERROR_AUDIO -> "آڈیو ریکارڈنگ میں مسئلہ پیش آیا۔ مائیک چیک کریں۔"
                        SpeechRecognizer.ERROR_NO_MATCH -> "کوئی واضح آواز سنائی نہیں دی۔ دوبارہ بولیں یا سیمپل آزمائیں۔"
                        SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
                            "انٹرنیٹ کنکشن کا مسئلہ۔ براہ کرم کنکشن چیک کریں۔"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                            "مائیکروفون کی اجازت درکار ہے۔"
                        else -> "آواز سننے میں رکاوٹ پیش آئی (کوڈ $error)۔"
                    }
                    onErrorMessage(msg)
                }

                override fun onResults(results: Bundle?) {
                    _rmsLevel.value = 0f
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val best = matches?.firstOrNull().orEmpty().trim()
                    _partialText.value = best
                    if (best.isNotBlank()) {
                        onFinalResult(best)
                    } else {
                        onErrorMessage("کوئی جملہ شناخت نہیں ہو سکا۔")
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val partial = partialResults
                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()
                        .orEmpty()
                    if (partial.isNotBlank()) {
                        _partialText.value = partial
                    }
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, localeTag)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }
            recognizer.startListening(intent)
        } catch (e: Exception) {
            _rmsLevel.value = 0f
            onErrorMessage("مائیک شروع نہ ہو سکا: ${e.localizedMessage ?: "نامعلوم خرابی"}")
        }
    }

    fun stopListening() {
        _rmsLevel.value = 0f
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
        } catch (_: Exception) {
        }
        speechRecognizer = null
    }

    fun speakEnglish(text: String, localeTag: String = "en-US", slow: Boolean = false) {
        if (!ttsReady || text.isBlank()) return
        val locale = Locale.forLanguageTag(localeTag)
        tts?.language = locale
        tts?.setSpeechRate(if (slow) 0.72f else 1.0f)
        _isSpeakingTts.value = true
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "callsathi_tts_${System.currentTimeMillis()}")
    }

    fun stopSpeaking() {
        _isSpeakingTts.value = false
        tts?.stop()
    }

    fun release() {
        stopListening()
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {
        }
        tts = null
    }
}

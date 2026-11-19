package com.example.data.ai

import com.example.data.local.UserPreferencesEntity
import com.example.domain.model.CallMode
import com.example.domain.model.CustomerIntent
import com.example.domain.model.PracticeCustomerPrompt
import com.example.domain.model.PracticeEvaluation
import com.example.domain.model.PracticeScenario
import com.example.domain.model.PracticeTurn
import com.example.domain.model.ReplyLength
import com.example.domain.model.ReplyRefinementAction
import com.example.domain.model.ReplyStyle
import com.example.domain.model.SuggestedReply
import com.example.domain.model.TranslationAndRepliesResult
import kotlinx.coroutines.delay

/**
 * Sample placeholder implementation used when Firebase AI Logic is not enabled.
 * Provides clearly labeled sample Urdu translations and 3 call-center English reply styles
 * with context-aware intent detection, compliance guidance, and pronunciation hints.
 */
class PlaceholderAiRepository : AiRepository {
    override val isPlaceholderMode: Boolean = true

    override suspend fun translateAndGenerateReplies(
        customerEnglish: String,
        mode: CallMode,
        recentHistory: List<LiveTranscriptSegment>,
        preferences: UserPreferencesEntity
    ): TranslationAndRepliesResult {
        delay(450)
        val normalized = customerEnglish.trim().lowercase()
        val intent = detectIntent(normalized)
        val complianceNote = detectComplianceNote(normalized)
        val lengthMode = runCatching { ReplyLength.valueOf(preferences.replyLength) }
            .getOrDefault(ReplyLength.MEDIUM)

        val (urdu, romanUrdu) = translateCustomerUtterance(normalized, customerEnglish)
        val replies = buildRepliesForIntent(intent, mode, normalized, lengthMode, recentHistory.size)

        return TranslationAndRepliesResult(
            customerEnglish = customerEnglish.trim(),
            customerUrdu = "[Sample AI] $urdu",
            customerRomanUrdu = "[Sample AI] $romanUrdu",
            intent = intent,
            complianceNote = complianceNote,
            replies = replies,
            isSampleAi = true
        )
    }

    override suspend fun refineReply(
        baseReply: SuggestedReply,
        action: ReplyRefinementAction,
        preferences: UserPreferencesEntity
    ): SuggestedReply {
        delay(250)
        return when (action) {
            ReplyRefinementAction.SHORTER -> {
                val shortened = baseReply.englishText
                    .split(".")
                    .firstOrNull { it.isNotBlank() }
                    ?.trim()
                    ?.plus(".")
                    ?: "Certainly, let me help you with that right away."
                baseReply.copy(
                    englishText = shortened,
                    urduMeaning = "[Sample AI - مختصر] جی بالکل، میں ابھی آپ کی مدد کرتا ہوں۔",
                    romanUrduMeaning = "[Sample AI - Short] Ji bilkul, main abhi aap ki madad karta hoon."
                )
            }
            ReplyRefinementAction.MORE_PROFESSIONAL -> {
                baseReply.copy(
                    englishText = "Thank you for bringing this to my attention. I will be glad to assist you promptly and ensure everything is handled accurately.",
                    urduMeaning = "[Sample AI - پروفیشنل] اس معاملے کی نشاندہی کرنے کا شکریہ۔ مجھے فوراً آپ کی مدد کر کے خوشی ہوگی۔",
                    romanUrduMeaning = "[Sample AI - Formal] Is maamlay ki nishandahi karne ka shukriya. Mujhe foran aap ki madad kar ke khushi hogi.",
                    pronunciationGuide = "Thank yoo for bring-ing this too my uh-ten-shun."
                )
            }
            ReplyRefinementAction.REGENERATE -> {
                baseReply.copy(
                    englishText = "I completely understand your point. Please allow me one moment to check the exact details so I can give you the best solution.",
                    urduMeaning = "[Sample AI - نیا جواب] میں آپ کی بات پوری طرح سمجھتا ہوں۔ براہ کرم مجھے ایک لمحہ دیں تاکہ میں درست تفصیلات چیک کر سکوں۔",
                    romanUrduMeaning = "[Sample AI - New] Main aap ki baat poori tarah samajhta hoon. Barah-e-karam aik lamha dein.",
                    pronunciationGuide = "Eye kum-pleet-lee un-der-stand yor point."
                )
            }
        }
    }

    override suspend fun nextPracticeCustomerTurn(
        scenario: PracticeScenario,
        turns: List<PracticeTurn>,
        preferences: UserPreferencesEntity
    ): PracticeCustomerPrompt {
        delay(350)
        val turnCount = turns.count { it.speakerIsCustomer }
        return when {
            turnCount == 0 -> PracticeCustomerPrompt(
                englishLine = scenario.openingLine,
                urduTranslation = "[Sample AI] ${scenario.openingLineUrdu}",
                suggestedHintEn = "Thank you for calling! My name is Hamza, and I would be happy to help you today."
            )
            turnCount == 1 -> PracticeCustomerPrompt(
                englishLine = "I have been waiting since yesterday and nobody gave me a clear update. Can you fix this right now?",
                urduTranslation = "[Sample AI] میں کل سے انتظار کر رہا ہوں اور کسی نے واضح معلومات نہیں دیں۔ کیا آپ ابھی یہ مسئلہ حل کر سکتے ہیں؟",
                suggestedHintEn = "I sincerely apologize for the wait. Let me look into your account right away so we can resolve this for you."
            )
            turnCount == 2 -> PracticeCustomerPrompt(
                englishLine = "Okay, thank you for checking. Just to confirm, will there be any extra charges on my next bill?",
                urduTranslation = "[Sample AI] ٹھیک ہے، چیک کرنے کا شکریہ۔ بس تصدیق کر دیں، کیا میرے اگلے بل میں کوئی اضافی چارجز ہوں گے؟",
                suggestedHintEn = "No worries at all. I will verify the exact billing details on your account and confirm clearly before making any change."
            )
            else -> PracticeCustomerPrompt(
                englishLine = "That makes sense. Thank you so much for being polite and helpful today!",
                urduTranslation = "[Sample AI] جی سمجھ آ گیا۔ آج اتنی شائستگی سے مدد کرنے کا بہت شکریہ!",
                suggestedHintEn = "You are very welcome! Is there anything else I can assist you with today?",
                isSessionComplete = true
            )
        }
    }

    override suspend fun evaluatePracticeSession(
        scenario: PracticeScenario,
        turns: List<PracticeTurn>,
        preferences: UserPreferencesEntity
    ): PracticeEvaluation {
        delay(450)
        val userTurns = turns.filter { !it.speakerIsCustomer }
        val avgWords = if (userTurns.isEmpty()) 0 else userTurns.sumOf { it.englishText.split(" ").size } / userTurns.size
        val baseScore = when {
            avgWords >= 12 -> 88
            avgWords >= 7 -> 79
            else -> 68
        }
        return PracticeEvaluation(
            speakingScore = baseScore,
            grammarScore = (baseScore - 3).coerceIn(55, 96),
            responseQualityScore = (baseScore + 4).coerceIn(60, 98),
            confidenceScore = (baseScore + 1).coerceIn(58, 95),
            summaryEn = "[Sample AI Estimate] Good polite tone! Your response acknowledged the customer clearly. Focus on keeping steady pacing and using polite reassurance phrases.",
            summaryUr = "[Sample AI تخمینہ] آپ کا لہجہ شائستہ اور اچھا تھا! کسٹمر کی بات تسلیم کرنے کا انداز بہتر ہے۔ جملوں کو مختصر اور پراعتماد رکھنے کی مشق جاری رکھیں۔",
            mistakesAndCorrections = listOf(
                "Avoid saying 'Wait a minute' -> Prefer 'Please allow me one moment while I check that.'",
                "Avoid 'I don't know' -> Prefer 'Let me verify that information for you right away.'"
            ),
            betterAlternatives = listOf(
                "I completely understand your concern, and I am happy to help resolve this today.",
                "Thank you for your patience. Let me confirm those details for you right now."
            ),
            isSampleAi = true
        )
    }

    private fun detectIntent(text: String): CustomerIntent = when {
        text.contains("thank you for calling") || text.contains("hello") || text.contains("good morning") -> CustomerIntent.GREETING
        text.contains("angry") || text.contains("terrible") || text.contains("waiting") || text.contains("problem") || text.contains("refund") || text.contains("cancel") -> CustomerIntent.COMPLAINT
        text.contains("expensive") || text.contains("not interested") || text.contains("too much") -> CustomerIntent.OBJECTION
        text.contains("send") || text.contains("details") || text.contains("interested") || text.contains("verify") -> CustomerIntent.INFO_REQUEST
        text.contains("?") || text.startsWith("can ") || text.startsWith("how ") || text.startsWith("why ") || text.startsWith("what ") -> CustomerIntent.QUESTION
        else -> CustomerIntent.GENERAL
    }

    private fun detectComplianceNote(text: String): String? = when {
        text.contains("ssn") || text.contains("card") || text.contains("bank") || text.contains("payment") ->
            "Compliance Reminder: Never guess billing terms or request restricted card PINs. Follow your company's approved verification script."
        text.contains("medical") || text.contains("prescription") || text.contains("doctor") ->
            "Regulated Topic Notice: Do not give medical advice. Use only your employer's approved healthcare script."
        text.contains("legal") || text.contains("lawsuit") || text.contains("attorney") ->
            "Regulated Topic Notice: Escalate legal inquiries to a supervisor using approved company procedures."
        else -> null
    }

    private fun translateCustomerUtterance(normalized: String, original: String): Pair<String, String> = when {
        normalized.contains("thank you for calling") && normalized.contains("help") ->
            "ہیلو، کال کرنے کا شکریہ۔ آج میں آپ کی کیا مدد کر سکتا ہوں؟" to
                "Hello, call karne ka shukriya. Aaj main aap ki kya madad kar sakta hoon?"
        normalized.contains("why you are interested") ->
            "کیا آپ بتا سکتے ہیں کہ آپ اس سروس میں دلچسپی کیوں رکھتے ہیں؟" to
                "Kya aap bata sakte hain ke aap is service mein dilchaspi kyun rakhte hain?"
        normalized.contains("bill") || normalized.contains("charge") || normalized.contains("refund") ->
            "مجھے اپنے بل پر ایک اضافی چارج نظر آیا ہے، براہ کرم اس کی وضاحت کریں اور اسے ٹھیک کریں۔" to
                "Mujhe apne bill par aik izafi charge nazar aaya hai, barah-e-karam is ki wazahat karein."
        normalized.contains("internet") || normalized.contains("not working") || normalized.contains("error") ->
            "میری سروس کل رات سے ٹھیک کام نہیں کر رہی، براہ کرم اسے چیک کرنے میں مدد کریں۔" to
                "Meri service kal raat se theek kaam nahi kar rahi, barah-e-karam check karein."
        normalized.contains("appointment") || normalized.contains("schedule") || normalized.contains("reschedule") ->
            "میں کل دوپہر کے لیے اپنی اپائنٹمنٹ کا وقت تبدیل کرنا چاہتا ہوں۔" to
                "Main kal dopeher ke liye apni appointment ka waqt tabdeel karna chahta hoon."
        normalized.contains("expensive") || normalized.contains("price") ->
            "یہ پلان مجھے تھوڑا مہنگا لگ رہا ہے، کیا اس میں کوئی رعایت یا سستا پلان موجود ہے؟" to
                "Yeh plan mujhe thora mehnga lag raha hai, kya koi discount mojood hai?"
        else ->
            "کسٹمر نے کہا: \"$original\" — وہ واضح معلومات اور فوری مدد چاہتے ہیں۔" to
                "Customer ne kaha: \"$original\" — woh wazeh maloomat aur fori madad chahte hain."
    }

    private fun buildRepliesForIntent(
        intent: CustomerIntent,
        mode: CallMode,
        normalized: String,
        length: ReplyLength,
        historySize: Int
    ): List<SuggestedReply> {
        val greetingVariation = if (historySize % 2 == 1) "I would be glad to help you today." else "How may I assist you right now?"
        return when (intent) {
            CustomerIntent.GREETING -> listOf(
                SuggestedReply(
                    style = ReplyStyle.SIMPLE,
                    englishText = "Hello! Thank you. I need help with my account today.",
                    urduMeaning = "ہیلو! شکریہ۔ مجھے آج اپنے اکاؤنٹ کے حوالے سے مدد چاہیے۔",
                    romanUrduMeaning = "Hello! Shukriya. Mujhe aaj apne account ke baray mein madad chahiye.",
                    pronunciationGuide = "Hel-lo! Thank yoo. Eye need help with my uh-kownt too-day."
                ),
                SuggestedReply(
                    style = ReplyStyle.PROFESSIONAL,
                    englishText = if (mode == CallMode.INTERVIEW_PREP) {
                        "Good day! Thank you for speaking with me. I am excited to discuss how I can contribute to your team."
                    } else {
                        "Hello! Thank you for reaching out. My name is Hamza, and $greetingVariation"
                    },
                    urduMeaning = "السلام علیکم! رابطہ کرنے کا شکریہ۔ آج میں آپ کی کس طرح مدد کر سکتا ہوں؟",
                    romanUrduMeaning = "Hello! Rabta karne ka shukriya. Aaj main aap ki kis tarah madad kar sakta hoon?",
                    pronunciationGuide = "Hel-lo! Thank yoo for ree-ching owt. How may eye uh-sist yoo?"
                ),
                SuggestedReply(
                    style = ReplyStyle.FRIENDLY,
                    englishText = "Hi there! Thanks so much for calling in. Let us get this sorted out for you right away!",
                    urduMeaning = "ہیلو! کال کرنے کا بہت شکریہ۔ آئیے ابھی آپ کا کام مکمل کرتے ہیں!",
                    romanUrduMeaning = "Hi! Call karne ka bohat shukriya. Aaiye abhi aap ka kaam mukammal karte hain!",
                    pronunciationGuide = "Hy thair! Thanks so much for caw-ling in."
                )
            )
            CustomerIntent.COMPLAINT -> listOf(
                SuggestedReply(
                    style = ReplyStyle.SIMPLE,
                    englishText = "I am very sorry for this problem. Let me check your account right now.",
                    urduMeaning = "اس مسئلے کے لیے مجھے بہت افسوس ہے۔ میں ابھی آپ کا اکاؤنٹ چیک کرتا ہوں۔",
                    romanUrduMeaning = "Is maslay ke liye mujhe bohat afsos hai. Main abhi aap ka account check karta hoon.",
                    pronunciationGuide = "Eye am ve-ree so-ree for this prob-lem. Let me chek now."
                ),
                SuggestedReply(
                    style = ReplyStyle.PROFESSIONAL,
                    englishText = if (length == ReplyLength.SHORT) {
                        "I sincerely apologize for the inconvenience. Let me review your details immediately."
                    } else {
                        "I sincerely apologize for the frustration this has caused. I understand how important this is, so please allow me one moment to verify your account details without guessing."
                    },
                    urduMeaning = "تکلیف کے لیے میں دل سے معذرت خواہ ہوں۔ براہ کرم مجھے ایک لمحہ دیں تاکہ میں درست تفصیلات چیک کر سکوں۔",
                    romanUrduMeaning = "Takleef ke liye dil se maazrat khwah hoon. Aik lamha dein main details check kar loon.",
                    pronunciationGuide = "Eye sin-seer-lee uh-pol-o-jize for the in-kun-veen-yuns."
                ),
                SuggestedReply(
                    style = ReplyStyle.FRIENDLY,
                    englishText = "I completely understand why you are upset, and I am right here to help fix this for you today.",
                    urduMeaning = "میں پوری طرح سمجھتا ہوں کہ آپ پریشان کیوں ہیں، اور میں آج یہ مسئلہ حل کرنے میں آپ کی مدد کروں گا۔",
                    romanUrduMeaning = "Main poori tarah samajhta hoon aap pareshan kyun hain, main abhi madad karta hoon.",
                    pronunciationGuide = "Eye kum-pleet-lee un-der-stand wy yoo ar up-set."
                )
            )
            CustomerIntent.OBJECTION -> listOf(
                SuggestedReply(
                    style = ReplyStyle.SIMPLE,
                    englishText = "I understand your concern about price. We also have a smaller plan for you.",
                    urduMeaning = "میں قیمت کے بارے میں آپ کی فکر سمجھتا ہوں۔ ہمارے پاس ایک چھوٹا پلان بھی ہے۔",
                    romanUrduMeaning = "Main qeemat ke baray mein aap ki fikar samajhta hoon. Hamare paas chota plan bhi hai.",
                    pronunciationGuide = "Eye un-der-stand yor kun-sern uh-bowt pryss."
                ),
                SuggestedReply(
                    style = ReplyStyle.PROFESSIONAL,
                    englishText = "That is a very fair point. Could you share which budget or feature matters most to you so I can recommend only the option that fits?",
                    urduMeaning = "آپ کی بات بالکل درست ہے۔ کیا آپ بتا سکتے ہیں کہ آپ کے لیے کون سا بجٹ یا فیچر سب سے اہم ہے؟",
                    romanUrduMeaning = "Aap ki baat bilkul durust hai. Aap ke liye kaun sa budget ya feature sab se ahem hai?",
                    pronunciationGuide = "That iz uh ve-ree fair point. Kood yoo shair yor bud-jet?"
                ),
                SuggestedReply(
                    style = ReplyStyle.FRIENDLY,
                    englishText = "Totally makes sense! Let us look at the most affordable option together with zero pressure.",
                    urduMeaning = "جی بالکل سمجھ آتا ہے! آئیے بغیر کسی دباؤ کے سب سے مناسب آپشن دیکھتے ہیں۔",
                    romanUrduMeaning = "Ji bilkul samajh aata hai! Aaiye sab se munasib option dekhte hain.",
                    pronunciationGuide = "Toh-tuh-lee mayks sens! Let us look tuh-ge-ther."
                )
            )
            else -> {
                val contextAwareProfessional = if (normalized.contains("interested")) {
                    "Certainly! I am interested in learning more about your service because I believe it could be very helpful for my needs."
                } else {
                    "Certainly! Could you please share your reference number or confirm what specific detail you would like me to check first?"
                }
                val contextAwareUrdu = if (normalized.contains("interested")) {
                    "بالکل! میں آپ کی سروس کے بارے میں مزید جاننا چاہتا ہوں کیونکہ میرا خیال ہے کہ یہ میری ضروریات کے لیے مفید ہو سکتی ہے۔"
                } else {
                    "جی بالکل! براہ کرم اپنا ریفرنس نمبر بتائیں یا وضاحت کریں کہ میں پہلے کون سی تفصیل چیک کروں؟"
                }
                listOf(
                    SuggestedReply(
                        style = ReplyStyle.SIMPLE,
                        englishText = "Yes, sure! Please tell me a little more so I can help you clearly.",
                        urduMeaning = "جی بالکل! براہ کرم تھوڑی مزید تفصیل بتائیں تاکہ میں واضح مدد کر سکوں۔",
                        romanUrduMeaning = "Ji bilkul! Thori mazeed tafseel batayein taake main madad kar sakoon.",
                        pronunciationGuide = "Yes, shoor! Pleez tel me uh li-tul mor."
                    ),
                    SuggestedReply(
                        style = ReplyStyle.PROFESSIONAL,
                        englishText = contextAwareProfessional,
                        urduMeaning = contextAwareUrdu,
                        romanUrduMeaning = "Bilkul! Main aap ki service ke baray mein mazeed janna chahta hoon / details check karta hoon.",
                        pronunciationGuide = "Ser-tun-lee! Kood yoo pleez shair yor dee-tails?"
                    ),
                    SuggestedReply(
                        style = ReplyStyle.FRIENDLY,
                        englishText = "Of course! I would love to walk you through this step by step. Ready whenever you are!",
                        urduMeaning = "جی ضرور! میں آپ کو مرحلہ وار سب سمجھا دوں گا۔ جب آپ تیار ہوں بتائیں!",
                        romanUrduMeaning = "Ji zaroor! Main aap ko step by step samjha doon ga.",
                        pronunciationGuide = "Uv kors! Eye wood luv too wawk yoo throo this."
                    )
                )
            }
        }
    }
}

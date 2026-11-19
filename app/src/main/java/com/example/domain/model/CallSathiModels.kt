package com.example.domain.model

enum class UiLanguage(val labelEn: String, val labelUr: String) {
    URDU("Urdu", "اردو"),
    ENGLISH("English", "English")
}

enum class EnglishLevel(val labelEn: String, val labelUr: String) {
    BEGINNER("Beginner", "ابتدائی (Beginner)"),
    INTERMEDIATE("Intermediate", "درمیانہ (Intermediate)"),
    ADVANCED("Advanced", "ماہر (Advanced)")
}

enum class CallMode(val labelEn: String, val labelUr: String) {
    CUSTOMER_SUPPORT("Customer Support", "کسٹمر سپورٹ"),
    SALES_CALLS("Sales Calls", "سیلز کالز"),
    APPOINTMENT_BOOKING("Appointment Booking", "اپائنٹمنٹ بکنگ"),
    TECHNICAL_SUPPORT("Technical Support", "ٹیکنیکل سپورٹ"),
    GENERAL_SERVICE("General Customer Service", "جنرل کسٹمر سروس"),
    INTERVIEW_PREP("Interview Preparation", "انٹرویو کی تیاری")
}

enum class AccentPreference(val labelEn: String, val labelUr: String, val localeTag: String) {
    AMERICAN("American (US)", "امریکی لہجہ (US)", "en-US"),
    BRITISH("British (UK)", "برطانوی لہجہ (UK)", "en-GB"),
    AUSTRALIAN("Australian (AU)", "آسٹریلوی لہجہ (AU)", "en-AU")
}

enum class ReplyLength(val labelEn: String, val labelUr: String) {
    SHORT("Short", "مختصر"),
    MEDIUM("Medium", "درمیانہ"),
    DETAILED("Detailed", "تفصیلی")
}

enum class ReplyTone(val labelEn: String, val labelUr: String) {
    FRIENDLY("Friendly", "دوستانہ"),
    PROFESSIONAL("Professional", "پروفیشنل"),
    FORMAL("Formal", "رسمی")
}

enum class UrduScriptType(val labelEn: String, val labelUr: String) {
    URDU_SCRIPT("Urdu Script (اردو)", "اردو رسم الخط"),
    ROMAN_URDU("Roman Urdu", "رومن اردو")
}

enum class PlanTier(val labelEn: String, val labelUr: String) {
    FREE_TRIAL("Free Trial", "مفت ٹرائل"),
    MONTHLY_PRO("Monthly Pro", "ماہانہ پرو"),
    YEARLY_PRO("Yearly Pro", "سالانہ پرو")
}

enum class PaymentMethodType(val labelEn: String, val labelUr: String, val isAutomated: Boolean) {
    JAZZCASH("JazzCash", "جاز کیش", false),
    EASYPAISA("Easypaisa", "ایزی پیسہ", false),
    BANK_TRANSFER("Pakistani Bank Transfer", "پاکستانی بینک ٹرانسفر", false),
    RAAST("Raast Instant Payment", "راست ادائیگی", false)
}

enum class PaymentStatus(val labelEn: String, val labelUr: String) {
    PENDING_REVIEW("Pending Admin Verification", "ایڈمن کی تصدیق جاری"),
    APPROVED("Approved & Active", "منظور شدہ"),
    REJECTED("Rejected", "مسترد شدہ")
}

enum class AccountStatus(val labelEn: String, val labelUr: String) {
    ACTIVE("Active", "فعال"),
    SUSPENDED("Suspended", "معطل")
}

enum class CustomerIntent(val labelEn: String, val labelUr: String) {
    GREETING("Greeting", "سلام / آغاز"),
    QUESTION("Question", "سوال"),
    COMPLAINT("Complaint", "شکایت"),
    OBJECTION("Objection", "اعتراض"),
    INFO_REQUEST("Info Request", "معلومات کی درخواست"),
    GENERAL("Conversation", "گفتگو")
}

enum class ReplyStyle(val labelEn: String, val labelUr: String) {
    SIMPLE("Simple English", "آسان انگلش"),
    PROFESSIONAL("Professional English", "پروفیشنل انگلش"),
    FRIENDLY("Friendly English", "دوستانہ انگلش")
}

enum class ReplyRefinementAction {
    REGENERATE,
    SHORTER,
    MORE_PROFESSIONAL
}

enum class ListeningStatus {
    IDLE,
    LISTENING,
    PAUSED,
    PROCESSING
}

enum class AssistantDeviceMode {
    MOBILE_SECOND_DEVICE,
    DESKTOP_CALL_WORKFLOW
}

data class SuggestedReply(
    val style: ReplyStyle,
    val englishText: String,
    val urduMeaning: String,
    val romanUrduMeaning: String,
    val pronunciationGuide: String
)

data class TranslationAndRepliesResult(
    val customerEnglish: String,
    val customerUrdu: String,
    val customerRomanUrdu: String,
    val intent: CustomerIntent,
    val complianceNote: String? = null,
    val replies: List<SuggestedReply>,
    val isSampleAi: Boolean = true
)

data class PracticeScenario(
    val id: String,
    val titleEn: String,
    val titleUr: String,
    val region: String,
    val difficulty: EnglishLevel,
    val openingLine: String,
    val openingLineUrdu: String
)

data class PracticeTurn(
    val speakerIsCustomer: Boolean,
    val englishText: String,
    val urduHint: String = ""
)

data class PracticeCustomerPrompt(
    val englishLine: String,
    val urduTranslation: String,
    val suggestedHintEn: String,
    val isSessionComplete: Boolean = false
)

data class PracticeEvaluation(
    val speakingScore: Int,
    val grammarScore: Int,
    val responseQualityScore: Int,
    val confidenceScore: Int,
    val summaryEn: String,
    val summaryUr: String,
    val mistakesAndCorrections: List<String>,
    val betterAlternatives: List<String>,
    val isSampleAi: Boolean = true
)

data class LessonItem(
    val id: String,
    val titleEn: String,
    val titleUr: String,
    val descriptionUr: String,
    val keyPhrases: List<LessonPhrase>,
    val exampleDialogueEn: String,
    val exampleDialogueUr: String,
    val quizQuestionEn: String,
    val quizOptions: List<String>,
    val correctOptionIndex: Int
)

data class LessonPhrase(
    val english: String,
    val urdu: String,
    val romanUrdu: String,
    val pronunciationGuide: String
)

data class VocabWord(
    val word: String,
    val meaningUr: String,
    val romanUrdu: String,
    val exampleEn: String
)

data class FaqItem(
    val questionEn: String,
    val questionUr: String,
    val answerEn: String,
    val answerUr: String
)

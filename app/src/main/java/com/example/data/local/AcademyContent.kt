package com.example.data.local

import com.example.domain.model.EnglishLevel
import com.example.domain.model.FaqItem
import com.example.domain.model.LessonItem
import com.example.domain.model.LessonPhrase
import com.example.domain.model.PracticeScenario
import com.example.domain.model.VocabWord

object AcademyContent {
    val sampleCustomerPrompts: List<Pair<String, String>> = listOf(
        "Support Greeting" to "Hello, thank you for calling. How can I help you today?",
        "Service Interest" to "Can you please tell me why you are interested in this service?",
        "Billing Complaint" to "I see an unexpected charge on my monthly bill and I am really upset about this!",
        "Appointment Change" to "Can I reschedule my appointment for tomorrow afternoon?",
        "Price Objection" to "Honestly, this monthly package seems too expensive for me right now."
    )

    val practiceScenarios: List<PracticeScenario> = listOf(
        PracticeScenario(
            id = "sc_usa_support",
            titleEn = "USA Customer Support",
            titleUr = "امریکی کسٹمر سپورٹ",
            region = "USA",
            difficulty = EnglishLevel.BEGINNER,
            openingLine = "Hi, I ordered an item three days ago from Texas and the tracking link hasn't updated yet.",
            openingLineUrdu = "ہیلو، میں نے تین دن پہلے ٹیکساس سے آرڈر کیا تھا اور ٹریکنگ لنک ابھی تک اپ ڈیٹ نہیں ہوا۔"
        ),
        PracticeScenario(
            id = "sc_uk_support",
            titleEn = "UK Customer Support",
            titleUr = "برطانوی کسٹمر سپورٹ",
            region = "UK",
            difficulty = EnglishLevel.BEGINNER,
            openingLine = "Hello mate, I'm calling from London regarding my broadband router—it keeps dropping every hour.",
            openingLineUrdu = "ہیلو، میں لندن سے اپنے انٹرنیٹ راؤٹر کے بارے میں کال کر رہا ہوں—یہ ہر گھنٹے بند ہو جاتا ہے۔"
        ),
        PracticeScenario(
            id = "sc_canada",
            titleEn = "Canadian Customer",
            titleUr = "کینیڈین کسٹمر",
            region = "Canada",
            difficulty = EnglishLevel.BEGINNER,
            openingLine = "Good morning! Could you help me update my billing address in Toronto?",
            openingLineUrdu = "گڈ مارننگ! کیا آپ ٹورنٹو میں میرا بلنگ ایڈریس اپ ڈیٹ کرنے میں مدد کر سکتے ہیں؟"
        ),
        PracticeScenario(
            id = "sc_australia",
            titleEn = "Australian Customer",
            titleUr = "آسٹریلوی کسٹمر",
            region = "Australia",
            difficulty = EnglishLevel.INTERMEDIATE,
            openingLine = "G'day! Just checking if my warranty covers accidental screen damage in Sydney.",
            openingLineUrdu = "ہیلو! میں چیک کر رہا ہوں کہ کیا میری وارنٹی اسکرین کے نقصان کو کور کرتی ہے؟"
        ),
        PracticeScenario(
            id = "sc_sales",
            titleEn = "Sales Representative",
            titleUr = "سیلز کال پریکٹس",
            region = "USA",
            difficulty = EnglishLevel.INTERMEDIATE,
            openingLine = "Why should I switch to your solar plan when my current provider is cheaper?",
            openingLineUrdu = "جب میرا موجودہ پرووائیڈر سستا ہے تو میں آپ کا سولر پلان کیوں لوں؟"
        ),
        PracticeScenario(
            id = "sc_angry",
            titleEn = "Angry Customer",
            titleUr = "ناراض کسٹمر کو سنبھالنا",
            region = "USA",
            difficulty = EnglishLevel.INTERMEDIATE,
            openingLine = "This is ridiculous! I've called three times and nobody has refunded my money yet!",
            openingLineUrdu = "یہ حد ہو گئی! میں تین بار کال کر چکا ہوں اور ابھی تک رقم واپس نہیں ملی!"
        ),
        PracticeScenario(
            id = "sc_complaint",
            titleEn = "Complaint Handling",
            titleUr = "شکایت کا حل",
            region = "UK",
            difficulty = EnglishLevel.BEGINNER,
            openingLine = "The parcel arrived damaged today. What can you do to fix this?",
            openingLineUrdu = "آج پارسل ٹوٹی ہوئی حالت میں ملا ہے۔ آپ اسے کیسے ٹھیک کریں گے؟"
        ),
        PracticeScenario(
            id = "sc_tech",
            titleEn = "Technical Support",
            titleUr = "ٹیکنیکل سپورٹ",
            region = "Canada",
            difficulty = EnglishLevel.ADVANCED,
            openingLine = "My dashboard shows Error 502 whenever I try to export my monthly invoice.",
            openingLineUrdu = "جب بھی میں اپنا ماہانہ بل ایکسپورٹ کرتا ہوں تو Error 502 آتا ہے۔"
        ),
        PracticeScenario(
            id = "sc_appointment",
            titleEn = "Appointment Scheduling",
            titleUr = "اپائنٹمنٹ بکنگ",
            region = "USA",
            difficulty = EnglishLevel.BEGINNER,
            openingLine = "Do you have any morning slots open for a consultation this Friday?",
            openingLineUrdu = "کیا اس جمعے کی صبح مشاورت کے لیے کوئی وقت خالی ہے؟"
        )
    )

    val lessons: List<LessonItem> = listOf(
        LessonItem(
            id = "l_greetings",
            titleEn = "Basic English Greetings",
            titleUr = "بنیادی انگلش سلام اور آغاز",
            descriptionUr = "امریکہ، برطانیہ اور آسٹریلیا کے کسٹمرز سے اعتماد کے ساتھ بات شروع کرنا سیکھیں۔",
            keyPhrases = listOf(
                LessonPhrase("Good morning! How may I assist you today?", "صبح بخیر! آج میں آپ کی کیا مدد کر سکتا ہوں؟", "Subah bakhair! Aaj main aap ki kya madad kar sakta hoon?", "Good mor-ning! How may eye uh-sist yoo?"),
                LessonPhrase("Thank you for calling, my name is Ali.", "کال کرنے کا شکریہ، میرا نام علی ہے۔", "Call karne ka shukriya, mera naam Ali hai.", "Thank yoo for caw-ling, my naym iz Ali.")
            ),
            exampleDialogueEn = "Customer: Hi, is this support?\nAgent: Yes! Good afternoon, thank you for calling. How may I help you today?",
            exampleDialogueUr = "کسٹمر: ہیلو، کیا یہ سپورٹ ہے؟\nایجنٹ: جی ہاں! کال کرنے کا شکریہ۔ آج میں آپ کی کیا مدد کر سکتا ہوں؟",
            quizQuestionEn = "Which greeting is best for an international call center?",
            quizOptions = listOf(
                "Yes, tell me fast.",
                "Good morning! Thank you for calling. How may I assist you today?",
                "Who are you?"
            ),
            correctOptionIndex = 1
        ),
        LessonItem(
            id = "l_answer_calls",
            titleEn = "How to Answer Phone Calls",
            titleUr = "فون کال اٹھانے کا پروفیشنل طریقہ",
            descriptionUr = "کال کے پہلے 10 سیکنڈ میں کسٹمر کا اعتماد جیتنے کے آسان جملے۔",
            keyPhrases = listOf(
                LessonPhrase("Thank you for holding. How can I help?", "انتظار کرنے کا شکریہ۔ میں کیا مدد کر سکتا ہوں؟", "Intizar karne ka shukriya. Main kya madad kar sakta hoon?", "Thank yoo for hohl-ding."),
                LessonPhrase("May I have your full name, please?", "کیا میں آپ کا پورا نام جان سکتا ہوں؟", "Kya main aap ka poora naam jaan sakta hoon?", "May eye hav yor fool naym, pleez?")
            ),
            exampleDialogueEn = "Agent: Thank you for calling Support Desk. May I have your name please?\nCustomer: Sure, it's Sarah.",
            exampleDialogueUr = "ایجنٹ: سپورٹ ڈیسک پر کال کرنے کا شکریہ۔ کیا میں آپ کا نام جان سکتا ہوں؟\nکسٹمر: جی، میرا نام سارہ ہے۔",
            quizQuestionEn = "How should you politely ask for the customer's name?",
            quizOptions = listOf(
                "Give me your name.",
                "May I have your full name, please?",
                "What's your name man?"
            ),
            correctOptionIndex = 1
        ),
        LessonItem(
            id = "l_introduce",
            titleEn = "How to Introduce Yourself",
            titleUr = "اپنا تعارف کروانا",
            descriptionUr = "انٹرویو اور کسٹمر کالز میں اپنا مختصر اور پروفیشنل تعارف۔",
            keyPhrases = listOf(
                LessonPhrase("My name is Sara, and I am your support specialist today.", "میرا نام سارہ ہے، اور آج میں آپ کی سپورٹ اسپیشلسٹ ہوں۔", "Mera naam Sara hai, aur aaj main aap ki support specialist hoon.", "My naym iz Sa-ra..."),
                LessonPhrase("I would be happy to guide you through this.", "مجھے اس میں آپ کی رہنمائی کر کے خوشی ہوگی۔", "Mujhe is mein aap ki rehnumai kar ke khushi hogi.", "Eye wood be ha-pee too gyd yoo.")
            ),
            exampleDialogueEn = "Agent: Hi! My name is Bilal, and I will be helping you with your booking today.",
            exampleDialogueUr = "ایجنٹ: ہیلو! میرا نام بلال ہے، اور آج میں آپ کی بکنگ میں مدد کروں گا۔",
            quizQuestionEn = "Which sentence sounds most confident and polite?",
            quizOptions = listOf(
                "I am Bilal, speak.",
                "My name is Bilal, and I will be happy to assist you today.",
                "Myself Bilal."
            ),
            correctOptionIndex = 1
        ),
        LessonItem(
            id = "l_professional",
            titleEn = "How to Speak Professionally",
            titleUr = "پروفیشنل انداز میں بات کرنا",
            descriptionUr = "روزمرہ کے جملوں کو کال سینٹر کی شائستہ انگلش میں بدلنا سیکھیں۔",
            keyPhrases = listOf(
                LessonPhrase("Please allow me one moment while I check that for you.", "براہ کرم مجھے ایک لمحہ دیں جب تک میں چیک کرتا ہوں۔", "Barah-e-karam aik lamha dein jab tak main check karta hoon.", "Pleez uh-low me wun moh-munt."),
                LessonPhrase("Thank you so much for your patience.", "آپ کے صبر کا بہت شکریہ۔", "Aap ke sabar ka bohat shukriya.", "Thank yoo for yor pay-shuns.")
            ),
            exampleDialogueEn = "Customer: Is it taking long?\nAgent: Thank you for your patience. I am checking your file right now.",
            exampleDialogueUr = "کسٹمر: کیا دیر لگے گی؟\nایجنٹ: آپ کے صبر کا شکریہ۔ میں ابھی آپ کی فائل چیک کر رہا ہوں۔",
            quizQuestionEn = "Instead of saying 'Wait', what is more professional?",
            quizOptions = listOf(
                "Stop talking and wait.",
                "Hold on.",
                "Please allow me one moment while I check that for you."
            ),
            correctOptionIndex = 2
        ),
        LessonItem(
            id = "l_angry",
            titleEn = "How to Handle Angry Customers",
            titleUr = "ناراض کسٹمر کو سنبھالنا",
            descriptionUr = "غصے میں آئے کسٹمر کو پرسکون کرنے کے آزمودہ جملے۔",
            keyPhrases = listOf(
                LessonPhrase("I completely understand your frustration.", "میں آپ کی پریشانی کو پوری طرح سمجھتا ہوں۔", "Main aap ki pareshani ko poori tarah samajhta hoon.", "Eye kum-pleet-lee un-der-stand yor frus-tray-shun."),
                LessonPhrase("I sincerely apologize for the inconvenience.", "اس تکلیف کے لیے میں دل سے معذرت خواہ ہوں۔", "Is takleef ke liye main dil se maazrat khwah hoon.", "Eye sin-seer-lee uh-pol-o-jize.")
            ),
            exampleDialogueEn = "Customer: This is terrible service!\nAgent: I sincerely apologize for the inconvenience. Let me fix this right away.",
            exampleDialogueUr = "کسٹمر: یہ بہت خراب سروس ہے!\nایجنٹ: تکلیف کے لیے معذرت خواہ ہوں۔ میں ابھی اسے حل کرتا ہوں۔",
            quizQuestionEn = "What is the best first step when a customer is angry?",
            quizOptions = listOf(
                "Argue back loudly.",
                "Acknowledge their feelings and apologize politely.",
                "Disconnect the call."
            ),
            correctOptionIndex = 1
        ),
        LessonItem(
            id = "l_questions",
            titleEn = "How to Ask Questions",
            titleUr = "کسٹمر سے سوالات پوچھنے کا طریقہ",
            descriptionUr = "معلومات یا ای میل تصدیق کرنے کے لیے شائستہ سوالات۔",
            keyPhrases = listOf(
                LessonPhrase("Could you please spell your last name for me?", "کیا آپ براہ کرم اپنے آخری نام کے ہجے بتا سکتے ہیں؟", "Kya aap apne aakhri naam ke hijjay bata sakte hain?", "Kood yoo pleez spel yor last naym?"),
                LessonPhrase("Just to confirm, is this your current email address?", "تصدیق کے لیے، کیا یہ آپ کا موجودہ ای میل ہے؟", "Tasdeeq ke liye, kya yeh aap ka mojooda email hai?", "Just too kun-ferm...")
            ),
            exampleDialogueEn = "Agent: Could you please verify your ZIP code for security?\nCustomer: Yes, 75001.",
            exampleDialogueUr = "ایجنٹ: کیا آپ سیکیورٹی کے لیے اپنا زپ کوڈ تصدیق کر سکتے ہیں؟\nکسٹمر: جی، 75001۔",
            quizQuestionEn = "How do you ask someone to repeat politely?",
            quizOptions = listOf(
                "Speak again!",
                "Could you please repeat that one more time?",
                "What?"
            ),
            correctOptionIndex = 1
        ),
        LessonItem(
            id = "l_ending",
            titleEn = "How to End a Phone Call",
            titleUr = "کال کو خوش اسلوبی سے ختم کرنا",
            descriptionUr = "کال کے اختتام پر بہترین تاثر چھوڑنے والے جملے۔",
            keyPhrases = listOf(
                LessonPhrase("Is there anything else I can assist you with today?", "کیا آج کوئی اور چیز ہے جس میں میں آپ کی مدد کر سکوں؟", "Kya aaj koi aur cheez hai jis mein main madad kar sakoon?", "Iz thair e-nee-thing els?"),
                LessonPhrase("Thank you for calling, and have a wonderful day!", "کال کرنے کا شکریہ، آپ کا دن خوشگوار گزرے!", "Call karne ka shukriya, aap ka din khushgawar guzray!", "Hav uh wun-der-ful day!")
            ),
            exampleDialogueEn = "Agent: Is there anything else I can help you with today?\nCustomer: No, that's all!\nAgent: Have a wonderful day!",
            exampleDialogueUr = "ایجنٹ: کیا کوئی اور مدد درکار ہے؟\nکسٹمر: نہیں، بس یہی تھا!\nایجنٹ: آپ کا دن اچھا گزرے!",
            quizQuestionEn = "Before hanging up, what should you always ask?",
            quizOptions = listOf(
                "Okay bye.",
                "Is there anything else I can assist you with today?",
                "Don't call again."
            ),
            correctOptionIndex = 1
        )
    )

    val dailyVocabulary: List<VocabWord> = listOf(
        VocabWord("Inconvenience", "تکلیف / زحمت", "Takleef / Zehmat", "Sorry for the inconvenience."),
        VocabWord("Verify", "تصدیق کرنا", "Tasdeeq karna", "Let me verify your email."),
        VocabWord("Promptly", "فوراً / بروقت", "Foran / Barwaqt", "We will resolve this promptly."),
        VocabWord("Escalate", "اعلیٰ افسر کو بھیجنا", "Aala afsar ko bhejna", "I can escalate this to a supervisor."),
        VocabWord("Appreciate", "قدر کرنا / شکر گزار ہونا", "Qadar karna", "I appreciate your patience today.")
    )

    val faqs: List<FaqItem> = listOf(
        FaqItem(
            questionEn = "Does CallSathi AI talk directly to the customer?",
            questionUr = "کیا CallSathi AI خود کسٹمر سے بات کرتا ہے؟",
            answerEn = "No! CallSathi AI never speaks to your customer and never auto-plays audio. It quietly listens (with your permission), translates to Urdu, and gives you 3 replies that YOU read and speak.",
            answerUr = "نہیں! CallSathi AI کسٹمر سے خود بات نہیں کرتا اور نہ آڈیو خود چلاتا ہے۔ آپ خود کسٹمر سے بولتے ہیں، ایپ صرف آپ کی مدد کے لیے ترجمہ اور جواب دکھاتی ہے۔"
        ),
        FaqItem(
            questionEn = "Can an Android app or website record phone calls directly?",
            questionUr = "کیا ایپ موبائل فون کال کی اندرونی آواز خود ریکارڈ کر سکتی ہے؟",
            answerEn = "Due to Android & mobile OS privacy rules, apps cannot intercept protected phone-call audio. On mobile, use speakerphone/second-device mode; on desktop, use USB headset or supported system audio.",
            answerUr = "موبائل OS کی پرائیویسی پابندیوں کی وجہ سے کوئی ایپ فون کال کی اندرونی آواز خود نہیں سن سکتی۔ موبائل پر اسپیکر یا دوسرے ڈیوائس کا مائیک موڈ استعمال کریں۔"
        ),
        FaqItem(
            questionEn = "How do JazzCash, Easypaisa, and Bank payments work?",
            questionUr = "جاز کیش، ایزی پیسہ اور بینک سے ادائیگی کیسے ہوتی ہے؟",
            answerEn = "Select your PKR plan, send payment to the official CallSathi account, upload your receipt screenshot, and an admin verifies it before activating your Pro plan.",
            answerUr = "اپنا پلان منتخب کریں، دیے گئے نمبر یا بینک میں رقم بھیجیں، رسید اپ لوڈ کریں۔ ایڈمن کی تصدیق کے بعد آپ کا پرو پلان فعال ہو جائے گا۔"
        )
    )
}

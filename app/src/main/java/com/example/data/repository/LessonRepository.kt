package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.DailyWisdom
import com.example.data.model.LessonSection
import com.example.data.model.MeditationSound
import com.example.data.model.SpiritualLesson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class LessonRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("spiritual_lessons_prefs", Context.MODE_PRIVATE)

    private val _lessons = MutableStateFlow<List<SpiritualLesson>>(emptyList())
    val lessons: StateFlow<List<SpiritualLesson>> = _lessons.asStateFlow()

    private val _dailyWisdomList = MutableStateFlow<List<DailyWisdom>>(emptyList())
    val dailyWisdomList: StateFlow<List<DailyWisdom>> = _dailyWisdomList.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val savedLessonsJson = prefs.getString("saved_custom_lessons", null)
        val defaultLessons = getInitialCuratedLessons()

        if (savedLessonsJson != null) {
            try {
                val customLessons = parseLessonsFromJson(savedLessonsJson)
                if (customLessons.isNotEmpty()) {
                    // Merge custom with default, keeping custom user modifications
                    val merged = mutableListOf<SpiritualLesson>()
                    merged.addAll(customLessons)
                    defaultLessons.forEach { def ->
                        if (merged.none { it.id == def.id }) {
                            merged.add(def)
                        }
                    }
                    _lessons.value = restoreUserData(merged)
                } else {
                    _lessons.value = restoreUserData(defaultLessons)
                }
            } catch (e: Exception) {
                _lessons.value = restoreUserData(defaultLessons)
            }
        } else {
            _lessons.value = restoreUserData(defaultLessons)
        }

        _dailyWisdomList.value = getInitialDailyWisdom()
    }

    private fun restoreUserData(baseLessons: List<SpiritualLesson>): List<SpiritualLesson> {
        val favorites = prefs.getStringSet("favorite_ids", emptySet()) ?: emptySet()
        val completed = prefs.getStringSet("completed_ids", emptySet()) ?: emptySet()

        return baseLessons.map { lesson ->
            val note = prefs.getString("note_${lesson.id}", lesson.userNote) ?: ""
            lesson.copy(
                isFavorite = favorites.contains(lesson.id),
                isCompleted = completed.contains(lesson.id),
                userNote = note
            )
        }
    }

    fun toggleFavorite(lessonId: String) {
        val current = _lessons.value.toMutableList()
        val index = current.indexOfFirst { it.id == lessonId }
        if (index != -1) {
            val item = current[index]
            val updated = item.copy(isFavorite = !item.isFavorite)
            current[index] = updated
            _lessons.value = current

            val favorites = prefs.getStringSet("favorite_ids", emptySet())?.toMutableSet() ?: mutableSetOf()
            if (updated.isFavorite) {
                favorites.add(lessonId)
            } else {
                favorites.remove(lessonId)
            }
            prefs.edit().putStringSet("favorite_ids", favorites).apply()
        }
    }

    fun toggleCompleted(lessonId: String) {
        val current = _lessons.value.toMutableList()
        val index = current.indexOfFirst { it.id == lessonId }
        if (index != -1) {
            val item = current[index]
            val newStatus = !item.isCompleted
            val updated = item.copy(
                isCompleted = newStatus,
                completionDate = if (newStatus) System.currentTimeMillis() else null
            )
            current[index] = updated
            _lessons.value = current

            val completed = prefs.getStringSet("completed_ids", emptySet())?.toMutableSet() ?: mutableSetOf()
            if (updated.isCompleted) {
                completed.add(lessonId)
            } else {
                completed.remove(lessonId)
            }
            prefs.edit().putStringSet("completed_ids", completed).apply()
        }
    }

    fun saveUserNote(lessonId: String, note: String) {
        val current = _lessons.value.toMutableList()
        val index = current.indexOfFirst { it.id == lessonId }
        if (index != -1) {
            val item = current[index]
            val updated = item.copy(userNote = note)
            current[index] = updated
            _lessons.value = current
            prefs.edit().putString("note_$lessonId", note).apply()
        }
    }

    fun addOrUpdateLesson(lesson: SpiritualLesson) {
        val current = _lessons.value.toMutableList()
        val index = current.indexOfFirst { it.id == lesson.id }
        if (index != -1) {
            current[index] = lesson
        } else {
            current.add(lesson)
        }
        _lessons.value = current
        saveAllLessonsToPrefs(current)
    }

    fun importLessonsFromText(rawText: String): Int {
        var count = 0
        try {
            // Try parsing as JSON array or JSON object
            val trimmed = rawText.trim()
            if (trimmed.startsWith("[") || trimmed.startsWith("{")) {
                val parsed = parseLessonsFromJson(trimmed)
                if (parsed.isNotEmpty()) {
                    parsed.forEach { addOrUpdateLesson(it) }
                    return parsed.size
                }
            }

            // Otherwise parse structured Markdown / Text format
            // Format:
            // # عنوان درس
            // دسته بندی: خودشناسی
            // ## بخش ۱: ...
            // متن بخش...
            val lesson = parseSingleTextLesson(rawText)
            if (lesson != null) {
                addOrUpdateLesson(lesson)
                count = 1
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return count
    }

    private fun parseSingleTextLesson(text: String): SpiritualLesson? {
        val lines = text.lines()
        var title = "درس معنوی جدید"
        var category = "خودشناسی"
        var subtitle = "آموزه معنوی و بیداری درون"
        var quote = "در سکون درون، حقیقت خود را آشکار می‌سازد."
        var quoteAuthor = "حکمت معنوی"
        var affirmation = "من در هر دم و بازدم، در آرامش و حضور الهی قرار دارم."
        val sections = mutableListOf<LessonSection>()
        val questions = mutableListOf<String>()

        var currentSectionTitle = "مقدمه و ژرف‌اندیشی"
        val currentSectionBody = StringBuilder()

        for (line in lines) {
            val trimmed = line.trim()
            when {
                trimmed.startsWith("# ") -> {
                    title = trimmed.removePrefix("# ").trim()
                }
                trimmed.startsWith("دسته بندی:") || trimmed.startsWith("دسته‌بندی:") || trimmed.startsWith("موضوع:") -> {
                    category = trimmed.substringAfter(":").trim()
                }
                trimmed.startsWith("زیرعنوان:") || trimmed.startsWith("توضیح:") -> {
                    subtitle = trimmed.substringAfter(":").trim()
                }
                trimmed.startsWith("نقل قول:") || trimmed.startsWith("حکمت:") -> {
                    quote = trimmed.substringAfter(":").trim()
                }
                trimmed.startsWith("عبارت تأکیدی:") || trimmed.startsWith("تأکید:") -> {
                    affirmation = trimmed.substringAfter(":").trim()
                }
                trimmed.startsWith("پرسش:") || trimmed.startsWith("- پرسش:") || trimmed.startsWith("سوال:") -> {
                    val q = trimmed.substringAfter(":").trim()
                    if (q.isNotEmpty()) questions.add(q)
                }
                trimmed.startsWith("## ") -> {
                    if (currentSectionBody.isNotEmpty()) {
                        sections.add(
                            LessonSection(
                                title = currentSectionTitle,
                                content = currentSectionBody.toString().trim()
                            )
                        )
                        currentSectionBody.clear()
                    }
                    currentSectionTitle = trimmed.removePrefix("## ").trim()
                }
                else -> {
                    if (trimmed.isNotEmpty()) {
                        currentSectionBody.append(trimmed).append("\n\n")
                    }
                }
            }
        }

        if (currentSectionBody.isNotEmpty()) {
            sections.add(
                LessonSection(
                    title = currentSectionTitle,
                    content = currentSectionBody.toString().trim()
                )
            )
        }

        if (sections.isEmpty()) {
            sections.add(
                LessonSection(
                    title = "متن اصلی درس",
                    content = text.trim()
                )
            )
        }

        if (questions.isEmpty()) {
            questions.add("این آموزه چگونه می‌تواند در زندگی روزمره من جاری شود؟")
            questions.add("در کدام بخش از روز می‌توانم سکون و آگاهی را تجربه کنم؟")
        }

        val nextNumber = (_lessons.value.maxOfOrNull { it.number } ?: 0) + 1
        return SpiritualLesson(
            id = "lesson_custom_${System.currentTimeMillis()}",
            number = nextNumber,
            title = title,
            subtitle = subtitle,
            category = category,
            durationMinutes = (text.length / 400).coerceIn(3, 20),
            quote = quote,
            quoteAuthor = quoteAuthor,
            sections = sections,
            reflectionQuestions = questions,
            dailyAffirmation = affirmation
        )
    }

    private fun parseLessonsFromJson(jsonStr: String): List<SpiritualLesson> {
        val list = mutableListOf<SpiritualLesson>()
        try {
            val jsonArray = if (jsonStr.trim().startsWith("[")) {
                JSONArray(jsonStr)
            } else {
                val obj = JSONObject(jsonStr)
                obj.optJSONArray("lessons") ?: JSONArray().put(obj)
            }

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val sectionsList = mutableListOf<LessonSection>()
                val sectionsArray = obj.optJSONArray("sections")
                if (sectionsArray != null) {
                    for (j in 0 until sectionsArray.length()) {
                        val secObj = sectionsArray.getJSONObject(j)
                        sectionsList.add(
                            LessonSection(
                                title = secObj.optString("title", "بخش ${j + 1}"),
                                content = secObj.optString("content", ""),
                                keyWisdom = secObj.optString("keyWisdom").takeIf { it.isNotEmpty() }
                            )
                        )
                    }
                }

                val questionsList = mutableListOf<String>()
                val qArray = obj.optJSONArray("reflectionQuestions")
                if (qArray != null) {
                    for (k in 0 until qArray.length()) {
                        questionsList.add(qArray.getString(k))
                    }
                }

                list.add(
                    SpiritualLesson(
                        id = obj.optString("id", "lesson_${i + 1}"),
                        number = obj.optInt("number", i + 1),
                        title = obj.optString("title", "درس معنوی"),
                        subtitle = obj.optString("subtitle", ""),
                        category = obj.optString("category", "خودشناسی"),
                        durationMinutes = obj.optInt("durationMinutes", 7),
                        quote = obj.optString("quote", ""),
                        quoteAuthor = obj.optString("quoteAuthor", "حکمت کهن"),
                        sections = sectionsList,
                        reflectionQuestions = questionsList,
                        dailyAffirmation = obj.optString("dailyAffirmation", ""),
                        recommendedFrequencyHz = obj.optInt("recommendedFrequencyHz", 432)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private fun saveAllLessonsToPrefs(lessons: List<SpiritualLesson>) {
        val array = JSONArray()
        for (lesson in lessons) {
            val obj = JSONObject()
            obj.put("id", lesson.id)
            obj.put("number", lesson.number)
            obj.put("title", lesson.title)
            obj.put("subtitle", lesson.subtitle)
            obj.put("category", lesson.category)
            obj.put("durationMinutes", lesson.durationMinutes)
            obj.put("quote", lesson.quote)
            obj.put("quoteAuthor", lesson.quoteAuthor)
            obj.put("dailyAffirmation", lesson.dailyAffirmation)
            obj.put("recommendedFrequencyHz", lesson.recommendedFrequencyHz)

            val secArray = JSONArray()
            lesson.sections.forEach { sec ->
                val sObj = JSONObject()
                sObj.put("title", sec.title)
                sObj.put("content", sec.content)
                sObj.put("keyWisdom", sec.keyWisdom ?: "")
                secArray.put(sObj)
            }
            obj.put("sections", secArray)

            val qArray = JSONArray()
            lesson.reflectionQuestions.forEach { qArray.put(it) }
            obj.put("reflectionQuestions", qArray)

            array.put(obj)
        }
        prefs.edit().putString("saved_custom_lessons", array.toString()).apply()
    }

    fun getMeditationSounds(): List<MeditationSound> {
        return listOf(
            MeditationSound(
                id = "432hz",
                title = "432 Hz Calm",
                persianTitle = "فرکانس آرامش ۴۳۲ هرتز",
                frequencyDescription = "هماهنگی با ضرباهنگ طبیعت و سکون ذهن",
                baseFrequencyHz = 432f,
                harmonicMultiplier = 1.0f
            ),
            MeditationSound(
                id = "528hz",
                title = "528 Hz Miracle",
                persianTitle = "فرکانس تحول ۵۲۸ هرتز",
                frequencyDescription = "گشایش قلب، پاکسازی ارتعاشات و عشق کیهانی",
                baseFrequencyHz = 528f,
                harmonicMultiplier = 1.22f
            ),
            MeditationSound(
                id = "singing_bowl",
                title = "Tibetan Singing Bowl",
                persianTitle = "طنین کاسه تبتی",
                frequencyDescription = "ارتعاشات ژرف کاسه تبتی برای حضور در لحظه",
                baseFrequencyHz = 216f,
                harmonicMultiplier = 1.5f
            ),
            MeditationSound(
                id = "om_resonance",
                title = "Om 136.1 Hz",
                persianTitle = "ارتعاش کیهانی ام (Om)",
                frequencyDescription = "فرکانس زمین برای ریشه‌داری و اتصال درونی",
                baseFrequencyHz = 136.1f,
                harmonicMultiplier = 2.0f
            ),
            MeditationSound(
                id = "stream_ambient",
                title = "Peaceful Stream",
                persianTitle = "نوای جریان آب و نسیم",
                frequencyDescription = "آرامش ملایم طبیعت و پالایش فکری",
                baseFrequencyHz = 200f,
                harmonicMultiplier = 1.0f
            )
        )
    }

    private fun getInitialDailyWisdom(): List<DailyWisdom> {
        return listOf(
            DailyWisdom(
                id = "dw_1",
                text = "«تو قطره‌ای در اقیانوس نیستی، بلکه تمام اقیانوسی هستی در یک قطره.»",
                source = "مولانا جلال‌الدین بلخی",
                category = "خودشناسی"
            ),
            DailyWisdom(
                id = "dw_2",
                text = "«پذیرش آنچه هست، نخستین گام به سوی رهایی از رنج و رسیدن به آرامش جاودان است.»",
                source = "اکهارت تله",
                category = "تسلیم و پذیرش"
            ),
            DailyWisdom(
                id = "dw_3",
                text = "«سکوت، زبانی است که خداوند با آن سخن می‌گوید؛ هر چیز دیگر تنها یک ترجمه ضعیف است.»",
                source = "شمس تبریزی",
                category = "سکوت و مراقبه"
            ),
            DailyWisdom(
                id = "dw_4",
                text = "«بخشش، رها کردن امید به داشتن گذشته‌ای متفاوت است؛ بخشش هدیه‌ای است به روان خودت.»",
                source = "حکمت معنوی",
                category = "بخشش و شفا"
            ),
            DailyWisdom(
                id = "dw_5",
                text = "«شکرگزاری پلی است نامرئی که قلب انسان را به بی‌کرانگی نعمات هستی پیوند می‌زند.»",
                source = "پیام‌آوران نور",
                category = "شکرگزاری"
            )
        )
    }

    private fun getInitialCuratedLessons(): List<SpiritualLesson> {
        return listOf(
            SpiritualLesson(
                id = "lesson_1",
                number = 1,
                title = "بیداری آگاهی و هنر حضور در لحظه حال",
                subtitle = "گذر از هیاهوی ذهن به ساحت امن و آرام اکنون",
                category = "حضور در لحظه",
                durationMinutes = 6,
                quote = "«گذشته جز خاطره‌ای در ذهن نیست و آینده جز تصویری در خیال؛ تنها حقیقت جاودانه، همین لحظه است.»",
                quoteAuthor = "حکمت حضور",
                recommendedFrequencyHz = 432,
                sections = listOf(
                    LessonSection(
                        title = "۱. زندان زمان روانی و افکار ناخودآگاه",
                        content = "بیشتر رنج‌های بشر از آنجا سرچشمه می‌گیرد که ذهن یا در حسرت و سرزنش‌های گذشته پرسه می‌زند یا در اضطراب و پیش‌بینی‌های آینده غرق است. این حالت «زمان روانی» نامیده می‌شود. وقتی یاد می‌گیریم توجهمان را از هجوم افکار جدا کنیم و به تنفس و حس بدن در همین ثانیه معطوف سازیم، پنجره‌ای به سوی آرامش بی‌پایان گشوده می‌شود.",
                        keyWisdom = "حضور، نفی گذشته یا آینده نیست؛ بلکه درک این است که زندگی فقط در اکنون جاری است."
                    ),
                    LessonSection(
                        title = "۲. تولد مشاهده‌گر بی‌قضاوت درون",
                        content = "تو افکارت نیستی؛ تو آگاهی و فضایی هستی که افکار در آن پدیدار و ناپدید می‌شوند. وقتی به جای هم‌ذات‌پنداری با نگرانی‌ها، مانند شاهدی مهربان به آنها نگاه می‌کنی، قدرت تخریبی احساسات منفی ناپدید می‌شود.",
                        keyWisdom = "به محض آنکه بفهمی فکر می‌کنی، میان تو و افکارت فاصله‌ای مقدس ایجاد می‌شود."
                    ),
                    LessonSection(
                        title = "۳. تمرین عملی لنگرگاه اکنون",
                        content = "چشمانت را به آرامی ببند. سه نفس عمیق و آگاهانه بکش. ضربان قلب و جریان حیات را در دستانت حس کن. هر صدایی که می‌شنوی، فقط بشنو بدون برچسب زدن خوب یا بد. این سادگی محض، خانه واقعی روح توست.",
                        keyWisdom = "در سکوت کامل، وجودت با کل هستی هماهنگ می‌گردد."
                    )
                ),
                reflectionQuestions = listOf(
                    "امروز در چه لحظاتی بیشترین دوری از اکنون و غرق شدن در ذهن را تجربه کردم؟",
                    "وقتی بدون قضاوت به احساساتم نگاه می‌کنم، چه تغییری در بار هیجانی آنها رخ می‌دهد؟"
                ),
                dailyAffirmation = "من هم‌اکنون در مرکز آرامش و حضور الهی مستقرم و تمام توجه خود را به زیبایی لحظه حال می‌سپارم."
            ),
            SpiritualLesson(
                id = "lesson_2",
                number = 2,
                title = "راز تسلیم و پذیرش؛ هماهنگی با جریان هستی",
                subtitle = "چگونه با رها کردن جنگ با واقعیت، به نیرویی بی‌کران دست یابیم",
                category = "تسلیم و پذیرش",
                durationMinutes = 7,
                quote = "«آب با نرمی خود بر سخت‌ترین سنگ‌ها پیروز می‌شود؛ تسلیم، انفعال نیست، بلکه اوج خرد و انعطاف روح است.»",
                quoteAuthor = "تائو ت چینگ",
                recommendedFrequencyHz = 528,
                sections = listOf(
                    LessonSection(
                        title = "۱. تفاوت تسلیم معنوی با درماندگی",
                        content = "تسلیم شدن به معنای تسلیم در برابر بی‌عدالتی یا دست کشیدن از تلاش هدفمند نیست. تسلیم یعنی دست کشیدن از مقاومت درونی بیهوده در برابر آنچه هم‌اکنون رخ داده است. وقتی اتفاقی افتاده، جنگیدن با واقعیت فقط رنج را دوچندان می‌کند.",
                        keyWisdom = "پذیرش عمیق، فضا را برای خردورزی و کنش الهام‌بخش باز می‌کند."
                    ),
                    LessonSection(
                        title = "۲. رهاسازی کنترل و اعتماد به هوشمندی کل",
                        content = "ایگو اصرار دارد همه چیز مطابق طرح محدود او پیش برود. اما وقتی درمی‌یابیم نظمی کیهانی و خیرخواهانه‌تر در جریان است، می‌توانیم مشت‌های گره‌کرده خود را باز کنیم و به جریان حکیمانه زندگی اعتماد ورزیم.",
                        keyWisdom = "با رها کردن چنگ‌اندازی به نتایج، امکان شکوفایی معجزات فراهم می‌شود."
                    ),
                    LessonSection(
                        title = "۳. مراقبه کلامی پذیرش",
                        content = "هرگاه با موقعیتی ناخوشایند مواجه شدی، در دل بگو: «این لحظه همین است که هست؛ من آن را با تمام وجود می‌پذیرم تا به روشنی ببینم گام درست بعدی چیست.» این کلام معجزه درونی خلق می‌کند.",
                        keyWisdom = "صلح درونی وابسته به شرایط بیرونی نیست؛ حاصل هماهنگی با جان جهان است."
                    )
                ),
                reflectionQuestions = listOf(
                    "در حال حاضر در زندگی با کدام موضوع در حال مقاومت و جنگ درونی هستم؟",
                    "اگر به جریان خیر و رشد در این چالش اعتماد کنم، چه احساسی در بدنم جاری می‌شود؟"
                ),
                dailyAffirmation = "من با آرامش در جریان رود خرد هستی شناورم و به خیر برتر در هر رخداد ایمان دارم."
            ),
            SpiritualLesson(
                id = "lesson_3",
                number = 3,
                title = "خودشناسی و دیدار با خویشتن اصیل",
                subtitle = "فراتر رفتن از نقاب شخصیت و کشف گوهر تابناک جان",
                category = "خودشناسی",
                durationMinutes = 8,
                quote = "«هر که خود را شناخت، پروردگار خویش را شناخته است.»",
                quoteAuthor = "حکمت کهن",
                recommendedFrequencyHz = 432,
                sections = listOf(
                    LessonSection(
                        title = "۱. نقاب ایگو در برابر خود حقیقی",
                        content = "از کودکی القاب، دارایی‌ها، ترس‌ها و داستان‌های گذشته هویتی ساختگی به نام «من اجتماعی» یا ایگو برای ما ساخته‌اند. ایگو همواره تشنه تأیید، نگران کمبود و هراسان از فناست. اما خود حقیقی تو ورای این نام‌ها و نشان‌ها، اقیانوسی از آگاهی سرمدی است.",
                        keyWisdom = "تو نقاشی نیستی؛ تو بوم شفافی هستی که نقاشی روی آن کشیده شده است."
                    ),
                    LessonSection(
                        title = "۲. کشف سکون لایتناهی در ژرفای قلب",
                        content = "همان‌طور که در عمق اقیانوس طوفان‌های سطحی اثری ندارند، در اعماق وجود تو مکانی مقدس وجود دارد که هیچ شکست، بیماری یا حرف دیگری نمی‌تواند به آن آسیبی برساند.",
                        keyWisdom = "به خلوت درون سفر کن؛ آنچه در جهان می‌جویی، در خودت نهفته است."
                    ),
                    LessonSection(
                        title = "۳. تمرین پرسش از خویشتن: من کیستم؟",
                        content = "چند لحظه با خود خلوت کن و بپرس: «وقتی همه نقش‌هایم، شغل، سوابق و برنامه‌های فردا را کنار بگذارم، آنچه باقی می‌ماند کیست؟» پاسخی از جنس کلمه نده؛ فقط بگذار حس حضور بدرخشد.",
                        keyWisdom = "شناخت گوهر جان، سرآغاز عشق حقیقی به همه آفریده‌هاست."
                    )
                ),
                reflectionQuestions = listOf(
                    "کدام باور محدودکننده درباره خودم را به اشتباه به عنوان هویت واقعی‌ام پذیرفته‌ام؟",
                    "چگونه می‌توانم از چشم‌انداز روح پاک و نامحدودم به زندگی نگاه کنم؟"
                ),
                dailyAffirmation = "من فراتر از افکار، ترس‌ها و القاب هستم؛ من نوری از آگاهی و آرامش الهی‌ام."
            ),
            SpiritualLesson(
                id = "lesson_4",
                number = 4,
                title = "قدرت شفابخش بخشش و گذشت",
                subtitle = "آزادسازی قلب از زنجیرهای سنگین کینه و بازگشت به سبکی روح",
                category = "بخشش و شفا",
                durationMinutes = 6,
                quote = "«نگه‌داشتن خشم و رنجش، مانند نوشیدن زهر و انتظار مرگ فرد دیگری است.»",
                quoteAuthor = "آموزه رهایی",
                recommendedFrequencyHz = 528,
                sections = listOf(
                    LessonSection(
                        title = "۱. حقیقت بخشش: رهایی خودت",
                        content = "بسیاری گمان می‌کنند بخشش به معنی تایید کار نادرست دیگری یا بازگشت به رابطه‌ای آسیب‌زا است. اما بخشش تصمیمی درونی برای رها کردن بار سمی خشم از دوش قلبت است تا بتوانی آزادانه نفس بکشی.",
                        keyWisdom = "بخشش لطفی به دیگران نیست؛ هدیه‌ای نجات‌بخش به روان خودت است."
                    ),
                    LessonSection(
                        title = "۲. شفقت در برابر ناآگاهی",
                        content = "هر کس بر اساس سطح آگاهی و زخم‌های التیام‌نیافته درونی‌اش عمل می‌کند. وقتی بفهمیم آزاری که دیده‌ایم ناشی از جهل و درد آن شخص بوده است، خشم جای خود را به درک و رهایی می‌دهد.",
                        keyWisdom = "انسان‌های زخم‌خورده، ناخواسته دیگران را زخمی می‌کنند؛ با بخشش چرخه درد را متوقف کن."
                    ),
                    LessonSection(
                        title = "۳. مراقبه بخشش خود و دیگران",
                        content = "تصویر خودت یا فردی که از او رنجش داری را در نور سپید تصور کن. با قلبی گشوده بگو: «من تو را می‌بخشم و رها می‌کنم. من اشتباهات گذشته خود را نیز در آغوش رحمت الهی می‌بخشم و رها می‌سازم.»",
                        keyWisdom = "قلبی که می‌بخشد، به چشمه‌ای جوشان از انرژی حیات تبدیل می‌شود."
                    )
                ),
                reflectionQuestions = listOf(
                    "چه رنجش کهنه‌ای هنوز در صندوقچه قلبم جا خوش کرده و انرژی‌ام را می‌کاهد؟",
                    "چه لطفی در حق خودم می‌توانم بکنم تا با بخشش خطاهای گذشته‌ام سبک‌بال شوم؟"
                ),
                dailyAffirmation = "قلب من سرشار از نور گذشت و شفقت است؛ من گذشته را رها کرده و آزادم."
            ),
            SpiritualLesson(
                id = "lesson_5",
                number = 5,
                title = "شکرگزاری؛ دروازه فراوانی و رزونانس کیهانی",
                subtitle = "دگرگونی ارتعاش وجودی با درک زیبایی‌های پنهان هستی",
                category = "شکرگزاری",
                durationMinutes = 5,
                quote = "«شکر نعمت، نعمتت افزون کند؛ کفر، نعمت از کفت بیرون کند.»",
                quoteAuthor = "مولانا",
                recommendedFrequencyHz = 432,
                sections = listOf(
                    LessonSection(
                        title = "۱. علم فرکانس سپاسگزاری",
                        content = "شکرگزاری بالاترین ارتعاش روحی را خلق می‌کند. وقتی بر داشته‌ها و زیبایی‌های موجود تمرکز می‌کنی، فرکانس کمبود و ترس در ذهنت ناپدید شده و زمینه برای جذب فراوانی و گشایش فراهم می‌گردد.",
                        keyWisdom = "آنچه را قدردانی می‌کنی، در مدار تجربه زندگی‌ات گسترش می‌یابد."
                    ),
                    LessonSection(
                        title = "۲. دیدن معجزات در دل روزمرگی‌ها",
                        content = "یک لیوان آب خنک، نفس کشیدن بدون درد، گرمای پرتو خورشید صبحگاهی و یک لبخند صمیمانه؛ هر کدام معجزاتی بزرگ‌اند که ذهن غافل به سادگی از کنارشان می‌گذرد. نگاهت را متبرک به دیدن شگفتی‌ها کن.",
                        keyWisdom = "چشم شکرگزار، در هر لحظه حضور خداوند را نظاره می‌کند."
                    ),
                    LessonSection(
                        title = "۳. آیین ۵ شکرگزاری شبانه",
                        content = "هر شب پیش از خواب، دست بر سینه بگذار و ۵ موهبت کوچک یا بزرگ امروز را به خاطر بیاور و حس لذت و قدردانی آن را در تمام سلول‌هایت جاری کن.",
                        keyWisdom = "خوابی که با شکرگزاری آغاز شود، سراسر آرامش و الهام خواهد بود."
                    )
                ),
                reflectionQuestions = listOf(
                    "امروز ۳ موهبت ساده و در عین حال شگفت‌انگیز که تجربه کردم چه بود؟",
                    "چگونه قدردانی از چالش‌های زندگی می‌تواند تبدیل به پله‌های رشد روحی من شود؟"
                ),
                dailyAffirmation = "من با تمام وجود برای تک‌تک لحظات و برکات زندگی شاکرم و در مدار فراوانی قرار دارم."
            ),
            SpiritualLesson(
                id = "lesson_6",
                number = 6,
                title = "سکوت مقدس و خلوت درون؛ زبان خاموش حقیقت",
                subtitle = "آشنایی با قدرت دگرگون‌ساز خاموشی ذهن و مراقبه عمیق",
                category = "سکوت و مراقبه",
                durationMinutes = 7,
                quote = "«در سکوت، چیزهایی را خواهی شنید که در میان همهمه‌ها هرگز به گوش جان نرسیده‌اند.»",
                quoteAuthor = "حکمت خاموشان",
                recommendedFrequencyHz = 432,
                sections = listOf(
                    LessonSection(
                        title = "۱. آلودگی صوتی ذهن و نیاز به روزه کلام",
                        content = "جهان امروز پر از سروصدا، اعلان‌ها و حرف‌های بی‌پایان است. ذهن برای بازیابی طراوت و دریافت الهامات معنوی، به زمان‌هایی از سکوت مطلق نیاز دارد. سکوت بیرون، سکوت درون را تقویت می‌کند.",
                        keyWisdom = "کلمات نقره‌اند، اما سکوت آگاهانه طلای خالص معرفت است."
                    ),
                    LessonSection(
                        title = "۲. ورود به فضای بی‌نهایت میان دو فکر",
                        content = "بین هر فکر تا فکر بعدی، شکافی از سکوت و آرامش مطلق وجود دارد. با تمرین مراقبه، این شکاف گسترده‌تر می‌شود و احساس یگانگی با مبدأ هستی متجلی می‌گردد.",
                        keyWisdom = "حقیقت داد نمی‌زند؛ در نجواهای سکوت به آرامی شنیده می‌شود."
                    ),
                    LessonSection(
                        title = "۳. تمرین روزانه ۱۰ دقیقه خاموشی کامل",
                        content = "گوشی را خاموش کن، در گوشه‌ای دنج بنشین و تنها نظاره‌گر ریتم موزون تنفس خود باش. بگذار تمام گردوغبار ذهن ته‌نشین شود تا آب زلال روانت دوباره شفاف گردد.",
                        keyWisdom = "هر چه درونت آرام‌تر باشد، حضورت در جهان موثرتر و پرصلابت‌تر خواهد بود."
                    )
                ),
                reflectionQuestions = listOf(
                    "آخرین باری که حداقل ۱۰ دقیقه در سکوت ناب و بدون هیچ وسیله الکترونیکی گذراندم کی بود؟",
                    "وقتی سکوت می‌کنم، چه پیام و هدایتی از درون به سویم سرازیر می‌شود؟"
                ),
                dailyAffirmation = "من در پناه سکوت درون با ژرفای حقیقت پیوند می‌خورم و به آرامشی تزلزل‌ناپذیر می‌رسم."
            ),
            SpiritualLesson(
                id = "lesson_7",
                number = 7,
                title = "عشق بی‌قید و شرط و پیوند با تمام کائنات",
                subtitle = "نگریستن به جهان از منظر شفقت و دیدن نور در چشمان همه هستی",
                category = "خودشناسی",
                durationMinutes = 6,
                quote = "«عشق آن نیست که به هم نگاه کنیم، بلکه آن است که هر دو به یک جهت بنگریم و پرتو یک نور را ببینیم.»",
                quoteAuthor = "پیام‌آوران خرد",
                recommendedFrequencyHz = 528,
                sections = listOf(
                    LessonSection(
                        title = "۱. عشق به عنوان سرشت بنیادین جهان",
                        content = "عشق معنوی یک احساس متغیر یا وابسته به بده‌بستان نیست؛ بلکه حالتی از بودن و اتصال قلبی با تمام ذرات کائنات است. وقتی این پیوستگی را حس می‌کنی، هر موجودی جلوه‌ای از همان نوری می‌شود که در خودت می‌تابد.",
                        keyWisdom = "دیدن وحدت در عین کثرت، سرآغاز مهر بی‌پایان است."
                    ),
                    LessonSection(
                        title = "۲. عبور از قضاوت به سوی همدلی عمیق",
                        content = "به جای برچسب زدن به دیگران، به یاد آوریم که هر کس در سفر روحی منحصر‌به‌فرد خود با درس‌هایی دست‌وپنجه نرم می‌کند. این نگاه، چشمه مهربانی بی‌پایانی را در روابطمان می‌گشاید.",
                        keyWisdom = "هر جا تاریکی دیدی، قضاوت نکن؛ شمعی از محبت روشن کن."
                    ),
                    LessonSection(
                        title = "۳. دعای خیر برای همه موجودات (متا)",
                        content = "روزی چند بار در قلب خود زمزمه کن: «امیدوارم همه موجودات شاد، در سلامت، در امان و در صلح و روشنایی زندگی کنند.» این ارتعاش، هاله نوری پیرامون روحت می‌گسترد.",
                        keyWisdom = "خیرخواهی برای دیگران، پیش از همه قلب خودت را از صفا و نور لبریز می‌سازد."
                    )
                ),
                reflectionQuestions = listOf(
                    "امروز چگونه می‌توانم یک کار محبت‌آمیز بدون چشمداشت برای کسی انجام دهم؟",
                    "آیا می‌توانم با چشمان عشق و درک به کسی که با او اختلاف نظر دارم بنگرم؟"
                ),
                dailyAffirmation = "قلب من کانون تابش عشق بی‌قیدوشرط است و صلح و برکت را به جهان هدیه می‌دهم."
            ),
            SpiritualLesson(
                id = "lesson_8",
                number = 8,
                title = "تاب‌آوری روحی و صلح در میان طوفان‌های زندگی",
                subtitle = "چگونه سختی‌ها و رنج‌ها را به سوخت رشد آگاهی تبدیل کنیم",
                category = "تسلیم و پذیرش",
                durationMinutes = 7,
                quote = "«زخم همان جایی است که نور به درون تو وارد می‌شود.»",
                quoteAuthor = "مولانا",
                recommendedFrequencyHz = 432,
                sections = listOf(
                    LessonSection(
                        title = "۱. کیمیاگری معنوی: تبدیل رنج به دانایی",
                        content = "رنج‌ها و موانع دشمنان تو نیستند؛ بلکه کاتالیزورهایی قدرتمند برای شکستن پوسته سفت ایگو و بیدار شدن خرد اصیل درون‌اند. همان‌طور که زغال سنگ تحت فشار به الماس بدل می‌شود، روح انسان نیز در بوته چالش‌ها صیقل می‌خورد.",
                        keyWisdom = "نپرس «چرا این اتفاق برای من افتاد؟» بپرس «این درس چه چیزی به جان من می‌افزاید؟»"
                    ),
                    LessonSection(
                        title = "۲. دژ تسخیرناپذیر سکون درونی",
                        content = "بادها ممکن است شاخه‌ها را تکان دهند، اما ریشه‌های کهنسال استوار می‌مانند. هر چه اتصالت به لایه عمیق وجودت قوی‌تر باشد، تلاطم‌های محیط بیرونی نمی‌توانند تعادل درونی‌ات را بر هم زنند.",
                        keyWisdom = "طوفان‌ها می‌آیند و می‌روند، اما آسمان آبی همیشه در ورای ابرها پابرجا است."
                    ),
                    LessonSection(
                        title = "۳. تمرین استواری در لحظات تنش",
                        content = "در هنگام بروز بحران، پیش از هر کلام یا عکس‌العملی، یک مکث سه ثانیه‌ای کن. پاهایت را روی زمین حس کن و از خود بپرس: «پاسخ مبتنی بر خرد و آرامش در این موقعیت چیست؟»",
                        keyWisdom = "شجاعت واقعی، حفظ آرامش و متانت در هنگامه آشوب است."
                    )
                ),
                reflectionQuestions = listOf(
                    "بزرگ‌ترین چالشی که در گذشته از سر گذراندم، چه هدیه روحی یا درسی به من بخشید؟",
                    "همین حالا چگونه می‌توانم استواری و وقار درونی خود را در برابر مسائل روز حفظ کنم؟"
                ),
                dailyAffirmation = "من نیرومند، آرام و ریشه‌دارم؛ هر چالشی در زندگی به شکوفایی نور درونم کمک می‌کند."
            )
        )
    }
}

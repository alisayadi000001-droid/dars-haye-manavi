package com.example.data.model

data class LessonSection(
    val title: String,
    val content: String,
    val keyWisdom: String? = null
)

data class SpiritualLesson(
    val id: String,
    val number: Int,
    val title: String,
    val subtitle: String,
    val category: String, // e.g. خودشناسی، حضور در لحظه، آرامش و پذیرش، شکرگزاری، سکوت درون
    val durationMinutes: Int,
    val quote: String,
    val quoteAuthor: String = "حکمت کهن",
    val sections: List<LessonSection>,
    val reflectionQuestions: List<String>,
    val dailyAffirmation: String,
    val recommendedFrequencyHz: Int = 432,
    val isFavorite: Boolean = false,
    val isCompleted: Boolean = false,
    val userNote: String = "",
    val completionDate: Long? = null
)

data class DailyWisdom(
    val id: String,
    val text: String,
    val source: String,
    val category: String
)

data class MeditationSound(
    val id: String,
    val title: String,
    val persianTitle: String,
    val frequencyDescription: String,
    val baseFrequencyHz: Float,
    val harmonicMultiplier: Float,
    val isPureTone: Boolean = false,
    val iconName: String = "lotus"
)

enum class SpiritualCategory(val persianName: String, val description: String) {
    ALL("همه درس‌ها", "تمام آموزه‌ها و حکمت‌ها"),
    SELF_KNOWLEDGE("خودشناسی", "شناخت من حقیقی و فراتر رفتن از ذهن"),
    PRESENCE("حضور در لحظه", "بیداری، سکون و زیستن در اکنون"),
    SURRENDER("تسلیم و پذیرش", "هماهنگی با جریان هستی و رهایی"),
    HEALING("بخشش و شفا", "رهاسازی رنجش‌ها و آزادی قلب"),
    GRATITUDE("شکرگزاری", "رزونانس فراوانی و پیوند کیهانی"),
    SILENCE("سکوت و مراقبه", "خلوت درون و شنیدن ندای جان")
}

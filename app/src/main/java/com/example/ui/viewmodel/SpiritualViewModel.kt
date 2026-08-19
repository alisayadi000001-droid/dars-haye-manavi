package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SpiritualAudioEngine
import com.example.data.model.DailyWisdom
import com.example.data.model.MeditationSound
import com.example.data.model.SpiritualCategory
import com.example.data.model.SpiritualLesson
import com.example.data.repository.LessonRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ReaderFontSize(val title: String, val scaleFactor: Float) {
    COMPACT("کوچک", 0.9f),
    STANDARD("متوسط", 1.0f),
    COMFORT("بزرگ", 1.15f),
    LARGE("خیلی بزرگ", 1.35f)
}

enum class NavigationScreen {
    HOME,
    LESSONS,
    LESSON_DETAIL,
    MEDITATION,
    JOURNAL,
    IMPORT_LESSONS
}

enum class BreathPhase(val persianTitle: String, val durationSec: Int) {
    INHALE("دَم عمیق (دریافت آگاهی)", 4),
    HOLD("حبس نفس (سکون و آرامش)", 4),
    EXHALE("بازدم آرام (رهاسازی تنش)", 6),
    REST("درنگ (حضور ناب)", 2)
}

class SpiritualViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LessonRepository(application)
    val audioEngine = SpiritualAudioEngine()

    private val _currentScreen = MutableStateFlow(NavigationScreen.HOME)
    val currentScreen: StateFlow<NavigationScreen> = _currentScreen.asStateFlow()

    private val _selectedLessonId = MutableStateFlow<String?>(null)
    val selectedLessonId: StateFlow<String?> = _selectedLessonId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(SpiritualCategory.ALL)
    val selectedCategory: StateFlow<SpiritualCategory> = _selectedCategory.asStateFlow()

    private val _onlyFavorites = MutableStateFlow(false)
    val onlyFavorites: StateFlow<Boolean> = _onlyFavorites.asStateFlow()

    private val _fontSize = MutableStateFlow(ReaderFontSize.STANDARD)
    val fontSize: StateFlow<ReaderFontSize> = _fontSize.asStateFlow()

    // Meditation Timer State
    private val _isMeditationRunning = MutableStateFlow(false)
    val isMeditationRunning: StateFlow<Boolean> = _isMeditationRunning.asStateFlow()

    private val _meditationRemainingSec = MutableStateFlow(300) // Default 5 mins
    val meditationRemainingSec: StateFlow<Boolean> = MutableStateFlow(false) // helper placeholder if needed
    val remainingSec: StateFlow<Int> = _meditationRemainingSec.asStateFlow()

    private val _meditationTotalSec = MutableStateFlow(300)
    val meditationTotalSec: StateFlow<Int> = _meditationTotalSec.asStateFlow()

    private val _currentBreathPhase = MutableStateFlow(BreathPhase.INHALE)
    val currentBreathPhase: StateFlow<BreathPhase> = _currentBreathPhase.asStateFlow()

    private val _breathPhaseSecondsLeft = MutableStateFlow(4)
    val breathPhaseSecondsLeft: StateFlow<Int> = _breathPhaseSecondsLeft.asStateFlow()

    private var meditationTimerJob: Job? = null
    private var breathJob: Job? = null

    // Ambient Sound State
    private val _isPlayingAmbient = MutableStateFlow(false)
    val isPlayingAmbient: StateFlow<Boolean> = _isPlayingAmbient.asStateFlow()

    private val _selectedSoundId = MutableStateFlow("432hz")
    val selectedSoundId: StateFlow<String> = _selectedSoundId.asStateFlow()

    private val _ambientVolume = MutableStateFlow(0.6f)
    val ambientVolume: StateFlow<Float> = _ambientVolume.asStateFlow()

    val meditationSounds: List<MeditationSound> = repository.getMeditationSounds()
    val dailyWisdomList: StateFlow<List<DailyWisdom>> = repository.dailyWisdomList

    private val _dailyWisdomIndex = MutableStateFlow(0)
    val dailyWisdomIndex: StateFlow<Int> = _dailyWisdomIndex.asStateFlow()

    // Combined filtered lessons
    val allLessons: StateFlow<List<SpiritualLesson>> = repository.lessons

    val filteredLessons: StateFlow<List<SpiritualLesson>> = combine(
        repository.lessons,
        _searchQuery,
        _selectedCategory,
        _onlyFavorites
    ) { lessons, query, category, favOnly ->
        lessons.filter { lesson ->
            val matchesQuery = query.isEmpty() ||
                    lesson.title.contains(query, ignoreCase = true) ||
                    lesson.subtitle.contains(query, ignoreCase = true) ||
                    lesson.sections.any { it.content.contains(query, ignoreCase = true) }
            val matchesCategory = category == SpiritualCategory.ALL ||
                    lesson.category == category.persianName
            val matchesFav = !favOnly || lesson.isFavorite

            matchesQuery && matchesCategory && matchesFav
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedLesson: StateFlow<SpiritualLesson?> = combine(
        repository.lessons,
        _selectedLessonId
    ) { list, id ->
        list.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun navigateTo(screen: NavigationScreen) {
        _currentScreen.value = screen
    }

    fun openLesson(lessonId: String) {
        _selectedLessonId.value = lessonId
        _currentScreen.value = NavigationScreen.LESSON_DETAIL
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: SpiritualCategory) {
        _selectedCategory.value = category
    }

    fun toggleFavoritesFilter() {
        _onlyFavorites.value = !_onlyFavorites.value
    }

    fun setFontSize(size: ReaderFontSize) {
        _fontSize.value = size
    }

    fun toggleLessonFavorite(lessonId: String) {
        repository.toggleFavorite(lessonId)
    }

    fun toggleLessonCompleted(lessonId: String) {
        repository.toggleCompleted(lessonId)
    }

    fun saveNote(lessonId: String, note: String) {
        repository.saveUserNote(lessonId, note)
    }

    fun nextDailyWisdom() {
        val total = dailyWisdomList.value.size
        if (total > 0) {
            _dailyWisdomIndex.value = (_dailyWisdomIndex.value + 1) % total
        }
    }

    fun toggleAmbientSound(soundId: String? = null) {
        val targetId = soundId ?: _selectedSoundId.value
        _selectedSoundId.value = targetId

        if (_isPlayingAmbient.value && (soundId == null || soundId == audioEngine.currentSoundId)) {
            audioEngine.stopSound()
            _isPlayingAmbient.value = false
        } else {
            val soundObj = meditationSounds.find { it.id == targetId }
            val freq = soundObj?.baseFrequencyHz ?: 432f
            audioEngine.volume = _ambientVolume.value
            audioEngine.startSound(targetId, freq)
            _isPlayingAmbient.value = true
        }
    }

    fun setAmbientVolume(vol: Float) {
        _ambientVolume.value = vol
        audioEngine.volume = vol
    }

    fun startMeditation(durationMinutes: Int = 5, soundId: String = "432hz") {
        stopMeditation()
        _meditationTotalSec.value = durationMinutes * 60
        _meditationRemainingSec.value = durationMinutes * 60
        _isMeditationRunning.value = true

        // Play beginning gong chime
        audioEngine.playChimeGong()

        // Start soothing frequency
        _selectedSoundId.value = soundId
        val soundObj = meditationSounds.find { it.id == soundId }
        val freq = soundObj?.baseFrequencyHz ?: 432f
        audioEngine.volume = _ambientVolume.value
        audioEngine.startSound(soundId, freq)
        _isPlayingAmbient.value = true

        // Breath rhythm loop
        startBreathLoop()

        // Meditation countdown timer
        meditationTimerJob = viewModelScope.launch {
            while (_meditationRemainingSec.value > 0 && _isMeditationRunning.value) {
                delay(1000)
                _meditationRemainingSec.value -= 1
            }
            if (_meditationRemainingSec.value <= 0) {
                // Play completion chime
                audioEngine.playChimeGong()
                stopMeditation()
            }
        }
    }

    private fun startBreathLoop() {
        breathJob?.cancel()
        breathJob = viewModelScope.launch {
            val phases = listOf(
                BreathPhase.INHALE,
                BreathPhase.HOLD,
                BreathPhase.EXHALE,
                BreathPhase.REST
            )
            var currentPhaseIndex = 0

            while (_isMeditationRunning.value) {
                val phase = phases[currentPhaseIndex]
                _currentBreathPhase.value = phase
                for (sec in phase.durationSec downTo 1) {
                    _breathPhaseSecondsLeft.value = sec
                    delay(1000)
                    if (!_isMeditationRunning.value) break
                }
                currentPhaseIndex = (currentPhaseIndex + 1) % phases.size
            }
        }
    }

    fun stopMeditation() {
        _isMeditationRunning.value = false
        meditationTimerJob?.cancel()
        meditationTimerJob = null
        breathJob?.cancel()
        breathJob = null
        audioEngine.stopSound()
        _isPlayingAmbient.value = false
    }

    fun importLessons(rawText: String): Pair<Boolean, String> {
        if (rawText.isBlank()) {
            return Pair(false, "لطفاً متن یا فایل درس را وارد کنید.")
        }
        val count = repository.importLessonsFromText(rawText)
        return if (count > 0) {
            Pair(true, "تعداد $count درس جدید با موفقیت به برنامه اضافه و ذخیره شد.")
        } else {
            Pair(false, "قالب متن شناسایی نشد. لطفاً ساختار عنوان و بخش‌ها را بررسی فرمایید.")
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
    }
}

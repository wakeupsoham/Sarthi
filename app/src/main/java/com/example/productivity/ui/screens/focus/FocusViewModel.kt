package com.example.productivity.ui.screens.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productivity.data.repository.FakeFocusRepository
import com.example.productivity.domain.model.FocusSession
import com.example.productivity.domain.model.SessionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

enum class FocusState {
    IDLE, RUNNING, PAUSED, BREAK_PENDING, BREAK_RUNNING
}

@HiltViewModel
class FocusViewModel @Inject constructor(
    private val focusRepository: FakeFocusRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FocusState.IDLE)
    val state = _state.asStateFlow()

    private val _timeLeft = MutableStateFlow(25 * 60L) // Seconds
    val timeLeft = _timeLeft.asStateFlow()

    private val _currentTaskTitle = MutableStateFlow<String?>(null)
    val currentTaskTitle = _currentTaskTitle.asStateFlow()

    private var timerJob: Job? = null
    private var sessionStartTime: LocalDateTime? = null
    
    // Configuration
    private val focusDuration = 25 * 60L
    private val breakDuration = 5 * 60L

    fun startFocus(taskTitle: String? = null) {
        _currentTaskTitle.value = taskTitle
        _state.value = FocusState.RUNNING
        sessionStartTime = LocalDateTime.now()
        startTimer()
    }

    fun pauseTimer() {
        _state.value = FocusState.PAUSED
        timerJob?.cancel()
    }

    fun resumeTimer() {
        _state.value = FocusState.RUNNING
        startTimer()
    }

    fun stopSession() {
        _state.value = FocusState.IDLE
        timerJob?.cancel()
        _timeLeft.value = focusDuration
        // Log abandoned session?
    }
    
    fun startBreak() {
        _state.value = FocusState.BREAK_RUNNING
        _timeLeft.value = breakDuration
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timeLeft.value > 0) {
                delay(1000L)
                _timeLeft.value--
            }
            onTimerFinished()
        }
    }

    private fun onTimerFinished() {
        if (_state.value == FocusState.RUNNING) {
            _state.value = FocusState.BREAK_PENDING
            // Save successful session
            saveSession(SessionType.FOCUS)
        } else if (_state.value == FocusState.BREAK_RUNNING) {
            _state.value = FocusState.IDLE
            _timeLeft.value = focusDuration
             // Save break session
            saveSession(SessionType.BREAK)
        }
    }
    
    private fun saveSession(type: SessionType) {
        viewModelScope.launch {
            focusRepository.saveSession(
                FocusSession(
                    taskId = _currentTaskTitle.value, // Using title as ID for prototype simplicity
                    startTime = sessionStartTime ?: LocalDateTime.now(),
                    endTime = LocalDateTime.now(),
                    durationMinutes = if (type == SessionType.FOCUS) 25 else 5,
                    isCompleted = true,
                    type = type
                )
            )
        }
    }
}

package com.example.productivity.ui.screens.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productivity.domain.model.Priority
import com.example.productivity.domain.model.Task
import com.example.productivity.domain.model.WorkType
import com.example.productivity.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val reminderManager: com.example.productivity.data.notification.ReminderManager
) : ViewModel() {

    // Form state
    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _estimatedEffort = MutableStateFlow(1) // Default 1 hour
    val estimatedEffort = _estimatedEffort.asStateFlow()

    private val _priority = MutableStateFlow(Priority.MEDIUM)
    val priority = _priority.asStateFlow()

    private val _dueDate = MutableStateFlow(LocalDateTime.now().plusDays(1))
    val dueDate = _dueDate.asStateFlow()

    // Start time defaults to 10:00 AM
    private val _startTime = MutableStateFlow(LocalTime.of(10, 0))
    val startTime = _startTime.asStateFlow()

    fun onTitleChange(newTitle: String) {
        _title.value = newTitle
    }

    fun onDescriptionChange(newDesc: String) {
        _description.value = newDesc
    }

    fun onEffortChange(newEffort: Int) {
        _estimatedEffort.value = newEffort
    }

    fun onPriorityChange(newPriority: Priority) {
        _priority.value = newPriority
    }

    fun onDueDateChange(newDate: LocalDateTime) {
        _dueDate.value = newDate
    }

    fun onStartTimeChange(newTime: LocalTime) {
        _startTime.value = newTime
    }

    // Calculate end time based on start time + estimated effort
    fun getEndTime(): LocalTime {
        return _startTime.value.plusHours(_estimatedEffort.value.toLong())
    }

    fun saveTask(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val newTask = Task(
                id = UUID.randomUUID().toString(),
                title = _title.value.ifBlank { "Untitled Task" },
                description = _description.value,
                priority = _priority.value,
                dueDate = _dueDate.value,
                workType = WorkType.PROJECT,
                estimatedEffort = _estimatedEffort.value
            )
            taskRepository.saveTask(newTask)

            // Prototype: Schedule a nudge for 10 minutes from now (for demo purposes)
            // Wrapped in try-catch to handle missing SCHEDULE_EXACT_ALARM permission on Android 12+
            try {
                reminderManager.scheduleNudge(
                    id = newTask.id.hashCode(),
                    title = "Focus Nudge",
                    message = "Ready to work on '${newTask.title}'?",
                    triggerAt = LocalDateTime.now().plusMinutes(10)
                )
            } catch (e: Exception) {
                // Silently ignore if scheduling fails (permission not granted)
            }

            onSuccess()
        }
    }
}

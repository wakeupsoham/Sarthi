package com.example.productivity.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productivity.domain.model.Task
import com.example.productivity.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val allTasks = taskRepository.getTasks()

    val tasksForSelectedDate: StateFlow<List<Task>> = combine(allTasks, _selectedDate) { tasks, date ->
        tasks.filter { task ->
            task.dueDate.toLocalDate().isEqual(date)
        }.sortedBy { it.dueDate }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }
}

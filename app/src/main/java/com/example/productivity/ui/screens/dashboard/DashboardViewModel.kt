package com.example.productivity.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productivity.domain.model.Task
import com.example.productivity.domain.repository.TaskRepository
import com.example.productivity.domain.usecase.TaskPriorityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val focusRepository: com.example.productivity.domain.repository.FocusRepository,
    private val taskPriorityUseCase: TaskPriorityUseCase,
    private val generateAIReportUseCase: com.example.productivity.domain.usecase.GenerateAIReportUseCase
) : ViewModel() {

    private val allTasksFlow = taskRepository.getTasks()
    private val sessionsFlow = focusRepository.getSessions()

    // Hot flow for the "All Tasks" list (hides completed)
    val tasks: StateFlow<List<Task>> = allTasksFlow.map { list ->
         list.filter { !it.isCompleted }
             .sortedByDescending { task -> taskPriorityUseCase.calculatePriorityScore(task) }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // Hot flow for the Priority Widget (includes completed for visual context)
    val priorityTasks: StateFlow<List<Task>> = allTasksFlow.map { list ->
        list.sortedByDescending { task -> taskPriorityUseCase.calculatePriorityScore(task) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val urgentTasksCount: StateFlow<Int> = allTasksFlow.map { list ->
        // Urgent count is High Priority + Due Soon
        taskPriorityUseCase.getPrioritizedTasks(list).first.count {
            taskPriorityUseCase.calculatePriorityScore(it) >= 70 // Threshold for "Urgent"
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0
    )
    
    val completedTasksCount: StateFlow<Int> = allTasksFlow.map { list ->
         list.count { it.isCompleted }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0
    )
    
    val inProgressTasksCount: StateFlow<Int> = allTasksFlow.map { list ->
         list.count { !it.isCompleted }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0
    )
    
    val totalTasksCount: StateFlow<Int> = allTasksFlow.map { list ->
         list.size
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0
    )
    
    val aiReport: StateFlow<String> = kotlinx.coroutines.flow.combine(allTasksFlow, sessionsFlow) { tasks, sessions ->
        generateAIReportUseCase(tasks, sessions)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = "Collecting your progress..."
    )
    
    // Mock Graph Data (e.g., tasks completed per day of week)
    val weeklyProgress = MutableStateFlow(listOf(2, 5, 3, 7, 4, 6, 2))

    fun onTaskCompletionToggle(task: Task, isCompleted: Boolean) {
        viewModelScope.launch {
            taskRepository.toggleTaskCompletion(task.id, isCompleted)
        }
    }
}

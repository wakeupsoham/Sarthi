package com.example.productivity.ui.screens.declutter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productivity.domain.model.Task
import com.example.productivity.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class DeclutterViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _unfinishedTasks = MutableStateFlow<List<Task>>(emptyList())
    val unfinishedTasks: StateFlow<List<Task>> = _unfinishedTasks.asStateFlow()

    private val _completedCount = MutableStateFlow(0)
    val completedCount: StateFlow<Int> = _completedCount.asStateFlow()

    init {
        loadDailyStats()
    }

    private fun loadDailyStats() {
        viewModelScope.launch {
            val allTasks = taskRepository.getTasks().first()
            val today = LocalDateTime.now().toLocalDate()
            
            _completedCount.value = allTasks.count { 
                it.isCompleted && it.createdAt.toLocalDate() == today 
            }
            
            _unfinishedTasks.value = allTasks.filter { 
                !it.isCompleted && it.dueDate.toLocalDate() <= today 
            }
        }
    }

    fun moveToTomorrow(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(dueDate = task.dueDate.plusDays(1))
            taskRepository.updateTask(updatedTask)
            _unfinishedTasks.value = _unfinishedTasks.value.filter { it.id != task.id }
        }
    }

    fun reschedule(task: Task, days: Long) {
        viewModelScope.launch {
            val updatedTask = task.copy(dueDate = task.dueDate.plusDays(days))
            taskRepository.updateTask(updatedTask)
            _unfinishedTasks.value = _unfinishedTasks.value.filter { it.id != task.id }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            taskRepository.deleteTask(taskId)
            _unfinishedTasks.value = _unfinishedTasks.value.filter { it.id != taskId }
        }
    }
}

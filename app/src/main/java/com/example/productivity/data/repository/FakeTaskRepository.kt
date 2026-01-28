package com.example.productivity.data.repository

import com.example.productivity.domain.model.Priority
import com.example.productivity.domain.model.Task
import com.example.productivity.domain.model.WorkType
import com.example.productivity.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeTaskRepository @Inject constructor() : TaskRepository {

    private val _tasks = MutableStateFlow<List<Task>>(
        listOf(
            Task(
                title = "Design Assets Export",
                description = "Export all figma assets for developer handoff",
                priority = Priority.HIGH,
                dueDate = LocalDateTime.now().plusHours(2),
                workType = WorkType.PROJECT
            ),
            Task(
                title = "HR Catch-Up Call",
                description = "Weekly sync with HR team",
                priority = Priority.MEDIUM,
                dueDate = LocalDateTime.now().plusDays(1),
                workType = WorkType.OTHER
            ),
             Task(
                title = "Study for Algorithms Exam",
                description = "Review graph theory and dynamic programming",
                priority = Priority.HIGH,
                dueDate = LocalDateTime.now().plusDays(2),
                workType = WorkType.EXAM
            )
        )
    )

    override fun getTasks(): Flow<List<Task>> = _tasks

    override fun getTask(id: String): Flow<Task?> {
        return _tasks.map { list -> list.find { it.id == id } }
    }

    override suspend fun saveTask(task: Task) {
        val currentList = _tasks.value.toMutableList()
        currentList.add(task)
        _tasks.value = currentList
    }

    override suspend fun updateTask(task: Task) {
        val currentList = _tasks.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == task.id }
        if (index != -1) {
            currentList[index] = task
            _tasks.value = currentList
        }
    }

    override suspend fun deleteTask(taskId: String) {
        val currentList = _tasks.value.toMutableList()
        currentList.removeAll { it.id == taskId }
        _tasks.value = currentList
    }
    
    override suspend fun toggleTaskCompletion(taskId: String, isCompleted: Boolean) {
         val currentList = _tasks.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == taskId }
        if (index != -1) {
            currentList[index] = currentList[index].copy(isCompleted = isCompleted)
            _tasks.value = currentList
        }
    }
}

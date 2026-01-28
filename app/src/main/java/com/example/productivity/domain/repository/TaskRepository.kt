package com.example.productivity.domain.repository

import com.example.productivity.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasks(): Flow<List<Task>>
    fun getTask(id: String): Flow<Task?>
    suspend fun saveTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(taskId: String)
    suspend fun toggleTaskCompletion(taskId: String, isCompleted: Boolean)
}

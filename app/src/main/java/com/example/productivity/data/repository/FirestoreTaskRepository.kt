package com.example.productivity.data.repository

import com.example.productivity.domain.model.Priority
import com.example.productivity.domain.model.Task
import com.example.productivity.domain.model.WorkType
import com.example.productivity.domain.repository.TaskRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreTaskRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : TaskRepository {

    private val userId: String
        get() = auth.currentUser?.uid ?: ""

    private val tasksCollection by lazy { firestore.collection("tasks") }

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override fun getTasks(): Flow<List<Task>> = callbackFlow {
        if (userId.isEmpty()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        try {
            val subscription = tasksCollection
                .whereEqualTo("userId", userId)
                .orderBy("dueDate", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }
                    
                    val tasks = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            Task(
                                id = doc.id,
                                title = doc.getString("title") ?: "",
                                description = doc.getString("description") ?: "",
                                priority = Priority.valueOf(doc.getString("priority") ?: "LOW"),
                                dueDate = LocalDateTime.parse(doc.getString("dueDate") ?: "", formatter),
                                workType = WorkType.valueOf(doc.getString("workType") ?: "OTHER"),
                                estimatedEffort = doc.getLong("estimatedEffort")?.toInt() ?: 1,
                                isCompleted = doc.getBoolean("isCompleted") ?: false,
                                createdAt = LocalDateTime.parse(doc.getString("createdAt") ?: "", formatter)
                            )
                        } catch (e: Exception) {
                            null
                        }
                    } ?: emptyList()
                    
                    trySend(tasks)
                }
                
            awaitClose { subscription.remove() }
        } catch (e: Exception) {
            trySend(emptyList())
            close()
        }
    }

    override fun getTask(id: String): Flow<Task?> = callbackFlow {
        val subscription = tasksCollection.document(id).addSnapshotListener { doc, error ->
            if (error != null) return@addSnapshotListener
            
            val task = doc?.let {
                try {
                    Task(
                        id = it.id,
                        title = it.getString("title") ?: "",
                        description = it.getString("description") ?: "",
                        priority = Priority.valueOf(it.getString("priority") ?: "LOW"),
                        dueDate = LocalDateTime.parse(it.getString("dueDate"), formatter),
                        workType = WorkType.valueOf(it.getString("workType") ?: "OTHER"),
                        estimatedEffort = it.getLong("estimatedEffort")?.toInt() ?: 1,
                        isCompleted = it.getBoolean("isCompleted") ?: false,
                        createdAt = LocalDateTime.parse(it.getString("createdAt"), formatter)
                    )
                } catch (e: Exception) {
                    null
                }
            }
            trySend(task)
        }
        awaitClose { subscription.remove() }
    }

    override suspend fun saveTask(task: Task) {
        if (userId.isEmpty()) return
        
        val taskData = hashMapOf(
            "userId" to userId,
            "title" to task.title,
            "description" to task.description,
            "priority" to task.priority.name,
            "dueDate" to task.dueDate.format(formatter),
            "workType" to task.workType.name,
            "estimatedEffort" to task.estimatedEffort,
            "isCompleted" to task.isCompleted,
            "createdAt" to task.createdAt.format(formatter)
        )
        
        tasksCollection.document(task.id).set(taskData).await()
    }

    override suspend fun updateTask(task: Task) {
        saveTask(task)
    }

    override suspend fun deleteTask(taskId: String) {
        tasksCollection.document(taskId).delete().await()
    }

    override suspend fun toggleTaskCompletion(taskId: String, isCompleted: Boolean) {
        tasksCollection.document(taskId).update("isCompleted", isCompleted).await()
    }
}

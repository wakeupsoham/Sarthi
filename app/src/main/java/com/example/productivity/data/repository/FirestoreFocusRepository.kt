package com.example.productivity.data.repository

import com.example.productivity.domain.model.FocusSession
import com.example.productivity.domain.model.SessionType
import com.example.productivity.domain.repository.FocusRepository
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
class FirestoreFocusRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : FocusRepository {

    private val userId: String
        get() = auth.currentUser?.uid ?: ""

    private val sessionsCollection by lazy { firestore.collection("focus_sessions") }
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override fun getSessions(): Flow<List<FocusSession>> = callbackFlow {
        if (userId.isEmpty()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        try {
            val subscription = sessionsCollection
                .whereEqualTo("userId", userId)
                .orderBy("startTime", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }
                    
                    val sessions = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            FocusSession(
                                id = doc.id,
                                taskId = doc.getString("taskId"),
                                startTime = LocalDateTime.parse(doc.getString("startTime") ?: "", formatter),
                                endTime = doc.getString("endTime")?.let { LocalDateTime.parse(it, formatter) },
                                durationMinutes = doc.getLong("durationMinutes")?.toInt() ?: 0,
                                isCompleted = doc.getBoolean("isCompleted") ?: false,
                                type = SessionType.valueOf(doc.getString("type") ?: "FOCUS")
                            )
                        } catch (e: Exception) {
                            null
                        }
                    } ?: emptyList()
                    
                    trySend(sessions)
                }
                
            awaitClose { subscription.remove() }
        } catch (e: Exception) {
            trySend(emptyList())
            close()
        }
    }

    override suspend fun saveSession(session: FocusSession) {
        if (userId.isEmpty()) return
        
        val sessionData = hashMapOf(
            "userId" to userId,
            "taskId" to session.taskId,
            "startTime" to session.startTime.format(formatter),
            "endTime" to session.endTime?.format(formatter),
            "durationMinutes" to session.durationMinutes,
            "isCompleted" to session.isCompleted,
            "type" to session.type.name
        )
        
        sessionsCollection.document(session.id).set(sessionData).await()
    }
}

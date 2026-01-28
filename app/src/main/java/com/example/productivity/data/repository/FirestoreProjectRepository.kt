package com.example.productivity.data.repository

import com.example.productivity.domain.model.Milestone
import com.example.productivity.domain.model.Project
import com.example.productivity.domain.model.ProjectType
import com.example.productivity.domain.repository.ProjectRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreProjectRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ProjectRepository {

    private val userId: String
        get() = auth.currentUser?.uid ?: ""

    private val projectsCollection by lazy { firestore.collection("projects") }
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override fun getProjects(): Flow<List<Project>> = callbackFlow {
        if (userId.isEmpty()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        try {
            val subscription = projectsCollection
                .whereEqualTo("userId", userId)
                .orderBy("endDate", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }
                    
                    val projects = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            @Suppress("UNCHECKED_CAST")
                            val milestonesData = doc.get("milestones") as? List<Map<String, Any>> ?: emptyList()
                            val milestones = milestonesData.map { m ->
                                Milestone(
                                    title = m["title"] as? String ?: "",
                                    deadline = LocalDate.parse(m["deadline"] as? String ?: "", dateFormatter),
                                    isCompleted = m["isCompleted"] as? Boolean ?: false
                                )
                            }

                            Project(
                                id = doc.id,
                                name = doc.getString("name") ?: "",
                                description = doc.getString("description") ?: "",
                                endDate = LocalDate.parse(doc.getString("endDate") ?: "", dateFormatter),
                                type = ProjectType.valueOf(doc.getString("type") ?: "PERSONAL"),
                                milestones = milestones,
                                progress = doc.getDouble("progress")?.toFloat() ?: 0f
                            )
                        } catch (e: Exception) {
                            null
                        }
                    } ?: emptyList()
                    
                    trySend(projects)
                }
                
            awaitClose { subscription.remove() }
        } catch (e: Exception) {
            trySend(emptyList())
            close()
        }
    }

    override suspend fun saveProject(project: Project) {
        if (userId.isEmpty()) return
        
        val projectData = hashMapOf(
            "userId" to userId,
            "name" to project.name,
            "description" to project.description,
            "endDate" to project.endDate.format(dateFormatter),
            "type" to project.type.name,
            "progress" to project.progress,
            "milestones" to project.milestones.map { m ->
                mapOf(
                    "title" to m.title,
                    "deadline" to m.deadline.format(dateFormatter),
                    "isCompleted" to m.isCompleted
                )
            }
        )
        
        projectsCollection.document(project.id).set(projectData).await()
    }
}

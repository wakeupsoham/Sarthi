package com.example.productivity.data.repository

import com.example.productivity.domain.model.Milestone
import com.example.productivity.domain.model.Project
import com.example.productivity.domain.model.ProjectType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeProjectRepository @Inject constructor() : com.example.productivity.domain.repository.ProjectRepository {
    
    private val _projects = MutableStateFlow<List<Project>>(
        listOf(
            Project(
                name = "OS Final Project",
                description = "Build a kernel module",
                endDate = LocalDate.now().plusWeeks(2),
                type = ProjectType.ACADEMIC,
                milestones = listOf(
                    Milestone(title = "Proposal", deadline = LocalDate.now().plusDays(2), isCompleted = true),
                    Milestone(title = "MVP", deadline = LocalDate.now().plusDays(7)),
                    Milestone(title = "Submission", deadline = LocalDate.now().plusWeeks(2))
                ),
                progress = 0.33f
            )
        )
    )

    override fun getProjects(): Flow<List<Project>> = _projects

    override suspend fun saveProject(project: Project) {
        val current = _projects.value.toMutableList()
        current.add(project)
        _projects.value = current
    }
}

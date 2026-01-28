package com.example.productivity.domain.repository

import com.example.productivity.domain.model.Project
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun getProjects(): Flow<List<Project>>
    suspend fun saveProject(project: Project)
}

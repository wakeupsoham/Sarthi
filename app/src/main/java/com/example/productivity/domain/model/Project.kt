package com.example.productivity.domain.model

import java.time.LocalDate
import java.util.UUID

enum class ProjectType {
    PERSONAL, ACADEMIC, HACKATHON
}

data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate,
    val type: ProjectType,
    val milestones: List<Milestone> = emptyList(),
    val progress: Float = 0f // 0.0 to 1.0
)

data class Milestone(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val deadline: LocalDate,
    val isCompleted: Boolean = false
)

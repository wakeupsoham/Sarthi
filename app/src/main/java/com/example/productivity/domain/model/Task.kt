package com.example.productivity.domain.model

import java.time.LocalDateTime
import java.util.UUID

enum class Priority {
    HIGH, MEDIUM, LOW
}

enum class WorkType {
    PROJECT, EXAM, HACKATHON, ASSIGNMENT, OTHER
}

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val priority: Priority,
    val dueDate: LocalDateTime,
    val workType: WorkType,
    val estimatedEffort: Int = 1, // Hours
    val isCompleted: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

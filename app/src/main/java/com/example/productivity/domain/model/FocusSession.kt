package com.example.productivity.domain.model

import java.time.LocalDateTime
import java.util.UUID

enum class SessionType {
    FOCUS, BREAK
}

data class FocusSession(
    val id: String = UUID.randomUUID().toString(),
    val taskId: String?,    // Nullable if "Free Focus"
    val startTime: LocalDateTime,
    val endTime: LocalDateTime? = null,
    val durationMinutes: Int,
    val isCompleted: Boolean,
    val type: SessionType
)

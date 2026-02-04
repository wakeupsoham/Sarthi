package com.example.productivity.domain.repository

interface GeminiRepository {
    suspend fun generateWeeklyInsights(
        completedTasks: Int,
        totalTasks: Int,
        completionRate: Double
    ): String
}

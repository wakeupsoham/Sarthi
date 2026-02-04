package com.example.productivity.data.repository

import com.example.productivity.BuildConfig
import com.example.productivity.domain.repository.GeminiRepository
import com.google.ai.client.generativeai.GenerativeModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiRepositoryImpl @Inject constructor() : GeminiRepository {

    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-pro",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    override suspend fun generateWeeklyInsights(
        completedTasks: Int,
        totalTasks: Int,
        completionRate: Double
    ): String {
        if (BuildConfig.GEMINI_API_KEY.isBlank()) {
              return "Insight: API Key missing. | Tip: Add your key to local.properties."
        }

        val prompt = """
            Analyze the user's weekly productivity:
            - Completed Tasks: $completedTasks
            - Total Tasks: $totalTasks
            - Completion Rate: ${(completionRate * 100).toInt()}%
            
            Provide a short, motivating insight (1 sentence) and a quick productivity tip (1 sentence).
            Format: "Insight: [text] | Tip: [text]"
        """.trimIndent()

        return try {
            val response = generativeModel.generateContent(prompt)
            response.text ?: "Insight: Keep going! | Tip: Stay focused."
        } catch (e: Exception) {
            "Insight: Great effort this week! | Tip: Plan your breaks."
        }
    }
}

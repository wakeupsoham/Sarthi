package com.example.productivity.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productivity.domain.repository.GeminiRepository
import com.example.productivity.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class WeeklyStatsViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val geminiRepository: GeminiRepository
) : ViewModel() {

    private val _insight = MutableStateFlow("Loading insights...")
    val insight: StateFlow<String> = _insight.asStateFlow()

    private val _tip = MutableStateFlow("Loading tip...")
    val tip: StateFlow<String> = _tip.asStateFlow()

    private val _completionRate = MutableStateFlow(0f)
    val completionRate: StateFlow<Float> = _completionRate.asStateFlow()

    init {
        loadWeeklyStats()
    }

    private fun loadWeeklyStats() {
        viewModelScope.launch {
            try {
                // Determine start of week (e.g., 7 days ago)
                val oneWeekAgo = LocalDateTime.now().minusDays(7)
                
                val allTasks = taskRepository.getTasks().first()
                val weeklyTasks = allTasks.filter { it.createdAt.isAfter(oneWeekAgo) }
                
                val total = weeklyTasks.size
                val completed = weeklyTasks.count { it.isCompleted }
                val rate = if (total > 0) completed.toDouble() / total else 0.0
                
                _completionRate.value = rate.toFloat()

                if (total > 0) {
                    val aiResponse = geminiRepository.generateWeeklyInsights(completed, total, rate)
                    parseAiResponse(aiResponse)
                } else {
                    _insight.value = "No tasks found for this week."
                    _tip.value = "Start adding tasks to see insights!"
                }
            } catch (e: Exception) {
                _insight.value = "Could not generate insights."
                _tip.value = "Please try again later."
            }
        }
    }

    private fun parseAiResponse(response: String) {
        // Expected format: "Insight: ... | Tip: ..."
        try {
            val parts = response.split("|")
            val insightText = parts.getOrNull(0)?.substringAfter("Insight:")?.trim() ?: response
            val tipText = parts.getOrNull(1)?.substringAfter("Tip:")?.trim() ?: "Keep pushing forward!"
            
            _insight.value = insightText
            _tip.value = tipText
        } catch (e: Exception) {
             _insight.value = response
             _tip.value = ""
        }
    }
}

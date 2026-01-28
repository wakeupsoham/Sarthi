package com.example.productivity.domain.usecase

import com.example.productivity.domain.model.Task
import com.example.productivity.domain.model.FocusSession
import java.time.LocalDateTime
import javax.inject.Inject

class GenerateAIReportUseCase @Inject constructor() {

    operator fun invoke(tasks: List<Task>, sessions: List<FocusSession>): String {
        val today = LocalDateTime.now().toLocalDate()
        val completedToday = tasks.filter { it.isCompleted && it.dueDate?.toLocalDate() == today }
        val pendingToday = tasks.filter { !it.isCompleted && it.dueDate?.toLocalDate() == today }
        val focusMinutes = sessions.filter { it.startTime.toLocalDate() == today }.sumOf { it.durationMinutes }

        return when {
            completedToday.size >= 3 && focusMinutes > 60 -> 
                "Incredible focus! You've conquered ${completedToday.size} tasks. Take a well-deserved break."
            completedToday.isNotEmpty() -> 
                "Good momentum. ${completedToday.size} tasks down. Keep that steady pace, you're doing great."
            pendingToday.isNotEmpty() -> 
                "You have ${pendingToday.size} tasks for today. Start with the smallest one to build flow."
            else -> 
                "A clear slate! Use this calm time to plan your next big project or simply rest."
        }
    }
}

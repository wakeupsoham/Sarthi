package com.example.productivity.domain.usecase

import com.example.productivity.domain.model.Priority
import com.example.productivity.domain.model.Task
import com.example.productivity.domain.model.WorkType
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

class TaskPriorityUseCase @Inject constructor() {

    // Returns a pair: (FocusList, BacklogList)
    fun getPrioritizedTasks(allTasks: List<Task>, dailyCapacityHours: Int = 6): Pair<List<Task>, List<Task>> {
        val openTasks = allTasks.filter { !it.isCompleted }
        
        // Calculate scores
        val scoredTasks = openTasks.map { task ->
            task to calculatePriorityScore(task)
        }.sortedByDescending { it.second }

        val focusList = mutableListOf<Task>()
        val backlogList = mutableListOf<Task>()
        var currentHours = 0

        for ((task, score) in scoredTasks) {
            val daysUntilDue = Duration.between(LocalDateTime.now(), task.dueDate).toDays()
            val isDueToday = daysUntilDue <= 0

            // Add if capacity allows OR if it's due today (Critical)
            if (currentHours + task.estimatedEffort <= dailyCapacityHours || isDueToday) {
                focusList.add(task)
                currentHours += task.estimatedEffort
            } else {
                backlogList.add(task)
            }
        }

        return Pair(focusList, backlogList)
    }

    fun calculatePriorityScore(task: Task): Int {
        var score = 0
        val now = LocalDateTime.now()
        val daysUntilDue = Duration.between(now, task.dueDate).toDays()

        // 1. Deadline Pressure (Max 50)
        score += when {
            daysUntilDue <= 0 -> 50 // Overdue or Due Today
            daysUntilDue <= 1 -> 40 // Due Tomorrow
            daysUntilDue <= 3 -> 30
            daysUntilDue <= 7 -> 15
            else -> 0
        }

        // 2. Task Type Importance (Max 30)
        score += when (task.workType) {
            WorkType.EXAM -> 30
            WorkType.HACKATHON -> 25
            WorkType.PROJECT -> 20
            WorkType.ASSIGNMENT -> 15
            WorkType.OTHER -> 5
        }

        // 3. User Urgency (Max 20)
        score += when (task.priority) {
            Priority.HIGH -> 20
            Priority.MEDIUM -> 10
            Priority.LOW -> 0
        }

        return score
    }
}

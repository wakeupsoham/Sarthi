package com.example.productivity.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.productivity.domain.model.Priority
import com.example.productivity.domain.model.Task
import com.example.productivity.ui.theme.PrimaryGreen
import com.example.productivity.ui.theme.PrimaryOrange
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val tasks by viewModel.tasksForSelectedDate.collectAsState()
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE d")
    val monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Upcoming",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            // Optional: Menu button (3 dots) could go here
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = selectedDate.format(monthFormatter).uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Date Strip
        DateStrip(
            selectedDate = selectedDate,
            onDateSelected = viewModel::onDateSelected
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Selected Date Header
        Surface(
            color = Color.Transparent,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
             Column {
                 Text(
                     text = "${tasks.size} Task${if (tasks.size != 1) "s" else ""}",
                     style = MaterialTheme.typography.bodySmall,
                     color = Color.Gray
                 )
                 Text(
                     text = selectedDate.format(dateFormatter),
                     style = MaterialTheme.typography.headlineMedium,
                     color = Color.White
                 )
             }
        }
        
        Timeline(tasks = tasks)
    }
}

@Composable
fun DateStrip(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    // Show 2 weeks starting from today - 2 days (to give context)
    val startDate = LocalDate.now().minusDays(2) 
    val days = (0..13).map { startDate.plusDays(it.toLong()) }
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(days) { date ->
            val isSelected = date == selectedDate
            val isToday = date == LocalDate.now()
            
            Column(
                modifier = Modifier
                    .width(58.dp)
                    .height(85.dp)
                    .background(
                        color = if (isSelected) com.example.productivity.ui.theme.PrimaryPink else Color(0xFF2C2C2E),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onDateSelected(date) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).uppercase(),
                    color = if (isSelected) Color.White else Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = date.dayOfMonth.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                if (isToday && !isSelected) {
                     Spacer(modifier = Modifier.height(4.dp))
                     Box(modifier = Modifier.size(4.dp).background(com.example.productivity.ui.theme.PrimaryPink, CircleShape))
                }
            }
        }
    }
}

@Composable
fun Timeline(tasks: List<Task>) {
    if (tasks.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No tasks for this day", color = Color.Gray)
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(tasks) { task ->
                TimelineItem(task = task)
            }
            
            // Placeholder for "Free Time" logic could be inserted here if we had calculated gaps
        }
    }
}

@Composable
fun TimelineItem(task: Task) {
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    val startTime = task.dueDate.format(timeFormatter)
    val endTime = task.dueDate.plusHours(task.estimatedEffort.toLong()).format(timeFormatter)
    
    // Determine color based on priority or type
    val cardColor = when(task.priority) {
        Priority.HIGH -> PrimaryGreen 
        Priority.MEDIUM -> Color(0xFF26C6DA) // Cyan
        Priority.LOW -> Color(0xFFE0E0E0) // Light Gray
    }
    
    val textColor = if (task.priority == Priority.HIGH) Color.Black else Color.Black

    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
    ) {
        // Time Column
        Column(
            modifier = Modifier.width(65.dp).padding(top = 10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(startTime, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(endTime.replace("AM","").replace("PM",""), color = Color.Gray, fontSize = 10.sp)
        }
        
        // Content
        Box(
            modifier = Modifier
                .weight(1f)
                .background(color = cardColor, shape = RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                         shape = RoundedCornerShape(50),
                         color = Color.White.copy(alpha=0.5f)
                    ) {
                        Text(
                            text = task.priority.name, 
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), 
                            fontSize = 10.sp, 
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    // Could add icon here
                    Text("• ${task.workType.name}", fontSize = 10.sp, color = textColor.copy(alpha=0.7f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = task.title, 
                    fontWeight = FontWeight.Bold, 
                    color = textColor,
                    fontSize = 16.sp
                )
                Text(
                    text = "Due: ${task.dueDate.toLocalDate()}", 
                    fontSize = 12.sp, 
                    color = textColor.copy(alpha=0.7f)
                )
            }
        }
    }
}

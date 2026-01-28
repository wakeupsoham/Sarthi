package com.example.productivity.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.productivity.domain.model.Priority
import com.example.productivity.domain.model.Task
import com.example.productivity.ui.components.CalmGradientCard
import com.example.productivity.ui.theme.CalmStudyTheme
import com.example.productivity.ui.theme.BackgroundDark
import com.example.productivity.ui.theme.PrimaryGreen
import com.example.productivity.ui.theme.PrimaryOrange
import com.example.productivity.ui.theme.PrimaryTeal
import com.example.productivity.ui.theme.TextSecondary
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun DashboardScreen(
    onFabClick: () -> Unit,
    onDeclutterClick: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val tasks by viewModel.tasks.collectAsState(initial = emptyList())
    val priorityTasks by viewModel.priorityTasks.collectAsState(initial = emptyList())
    val urgentCount by viewModel.urgentTasksCount.collectAsState()
    val completedCount by viewModel.completedTasksCount.collectAsState()
    val inProgressCount by viewModel.inProgressTasksCount.collectAsState()
    val totalCount by viewModel.totalTasksCount.collectAsState()
    val aiReport by viewModel.aiReport.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onFabClick,
                containerColor = PrimaryOrange,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task", tint = Color.White)
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                DashboardHeader(name = "Student") 
            }

            item {
                DailySummaryCard(
                    urgentCount = urgentCount, 
                    aiReport = aiReport,
                    onDeclutterClick = onDeclutterClick
                )
            }

            item {
                Row(
                   modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min), // Intrinsic height for equal columns
                   horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Column: Priority Widget
                    PriorityWidget(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        tasks = priorityTasks,
                        onTaskCompletionToggle = viewModel::onTaskCompletionToggle
                    )
                    
                    // Right Column: Progress & InProgress
                    Column(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ProgressWidget(
                             modifier = Modifier.weight(1f).fillMaxWidth(),
                             completed = completedCount, 
                             total = totalCount
                        )
                        InProgressWidget(
                             modifier = Modifier.weight(1f).fillMaxWidth(),
                             count = inProgressCount
                        )
                    }
                }
            }
            
            item {
                 Text(
                    text = "All Tasks",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            items(tasks) { task ->
                TaskCard(task)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun DashboardHeader(name: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = "Hello \uD83D\uDC4B", style = MaterialTheme.typography.titleMedium, color = Color.White)
            Text(
                text = "$name!",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        }
    }
}

@Composable
fun DailySummaryCard(urgentCount: Int, aiReport: String, onDeclutterClick: () -> Unit) {
    CalmGradientCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // TODO: Date Format
                Text(text = "\uD83D\uDCC5 Today", color = Color.White) 
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.2f),
                    onClick = onDeclutterClick
                ) {
                    Text(
                        text = "Declutter",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
            
            Column {
                Text(text = aiReport, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f), maxLines = 2)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You Have $urgentCount\nTasks Urgent for Today.",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PriorityWidget(
    modifier: Modifier = Modifier,
    tasks: List<Task>,
    onTaskCompletionToggle: (Task, Boolean) -> Unit
) {
    Surface(
        modifier = modifier.height(320.dp), // Increased height to match reference style
        shape = RoundedCornerShape(24.dp),
        color = PrimaryGreen 
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Priority Task", style = MaterialTheme.typography.titleMedium, color = Color.Black, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Show top 5 priority tasks (scrolling handled by parent if needed, but simple lists prefer fixed items)
                tasks.take(5).forEach { task ->
                    PriorityItem(
                        text = task.title,
                        checked = task.isCompleted,
                        onCheckedChange = { isChecked ->
                            onTaskCompletionToggle(task, isChecked)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PriorityItem(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) { 
    val textColor = if (checked) Color.Black.copy(alpha = 0.4f) else Color.Black
    val textDecoration = if (checked) androidx.compose.ui.text.style.TextDecoration.LineThrough else null

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().clickable { onCheckedChange(!checked) }
    ) {
        RadioButton(
            selected = checked,
            onClick = { onCheckedChange(!checked) },
            colors = RadioButtonDefaults.colors(
                selectedColor = Color.Black.copy(alpha=0.6f),
                unselectedColor = Color.Black.copy(alpha = 0.5f)
            ),
            modifier = Modifier.size(20.dp) // Smaller radio button
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium, // Slightly larger
            maxLines = 1,
            color = textColor,
            textDecoration = textDecoration
        )
    }
}

@Composable
fun ProgressWidget(modifier: Modifier = Modifier, completed: Int, total: Int) {
    val percentage = if (total > 0) (completed.toFloat() / total.toFloat()) else 0f
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = BackgroundDark // Using Dark BG for contrast
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
              Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
              ) {
                  // Text Info
                  Column {
                      Text("Completed", color = TextSecondary, fontSize = 12.sp)
                      Text("$completed/$total task", color = Color.White, fontWeight = FontWeight.Bold)
                  }
                  
                  // Circular Indicator
                  Box(contentAlignment = Alignment.Center) {
                      CircularProgressIndicator(
                          progress = { 1f },
                          modifier = Modifier.size(50.dp),
                          color = Color.Gray.copy(alpha = 0.2f),
                          strokeWidth = 4.dp,
                      )
                      CircularProgressIndicator(
                          progress = { percentage },
                          modifier = Modifier.size(50.dp),
                          color = com.example.productivity.ui.theme.PrimaryPink, // Pink/Red accent
                          strokeWidth = 4.dp,
                      )
                      Text("${(percentage * 100).toInt()}%", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  }
              }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Little bar chart visual (static for now, just aesthetic)
            Row(
                modifier = Modifier.fillMaxWidth().height(20.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                listOf(0.4f, 0.6f, 0.3f, 0.8f, 0.5f, 0.7f, 0.4f).forEach { height ->
                    Box(
                         modifier = Modifier
                             .weight(1f)
                             .fillMaxHeight(height)
                             .clip(RoundedCornerShape(4.dp))
                             .background(com.example.productivity.ui.theme.PrimaryTeal)
                    )
                }
            }
        }
    }
}

@Composable
fun InProgressWidget(modifier: Modifier = Modifier, count: Int) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = BackgroundDark 
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Surface(
                shape = CircleShape,
                color = Color.Gray.copy(alpha = 0.2f),
                modifier = Modifier.size(40.dp)
            ) {
                 Icon(
                     imageVector = Icons.Default.LocalFireDepartment, // Flame icon approximation
                     contentDescription = null,
                     modifier = Modifier.padding(8.dp),
                     tint = com.example.productivity.ui.theme.PrimaryGreen // Green tint
                 )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("In Progress", color = Color.White, fontWeight = FontWeight.Bold)
                Text("$count task", color = TextSecondary, fontSize = 12.sp)
            }
        }
    }
}

// Helper function to get priority color
fun getPriorityColor(priority: Priority): Color {
    return when (priority) {
        Priority.HIGH -> PrimaryGreen
        Priority.MEDIUM -> PrimaryOrange
        Priority.LOW -> PrimaryTeal
    }
}

@Composable
fun TaskCard(task: Task) {
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    val dateFormatter = DateTimeFormatter.ofPattern("MMMM d")
    
    // Calculate start and end time based on estimated effort
    val startTime = LocalTime.of(9, 30)
    val endTime = startTime.plusHours(task.estimatedEffort.toLong())
    val timeRange = "${startTime.format(timeFormatter)} - ${endTime.format(timeFormatter)}"
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Top row: Priority badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Priority Badge
                Surface(
                    shape = RoundedCornerShape(50),
                    color = getPriorityColor(task.priority)
                ) {
                    Text(
                        text = task.priority.name.lowercase().replaceFirstChar { it.uppercase() },
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Task Title
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Time with clock icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = timeRange,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // Due Date
            Text(
                text = "Due Date: ${task.dueDate.format(dateFormatter)}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

// Keep old TaskItem for backwards compatibility if needed elsewhere
@Composable
fun TaskItem(task: Task) {
    TaskCard(task)
}

@Preview
@Composable
fun DashboardPreview() {
    CalmStudyTheme {
        DashboardScreen(onFabClick = {}, onDeclutterClick = {})
    }
}

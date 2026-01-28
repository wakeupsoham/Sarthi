package com.example.productivity.ui.screens.task

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.productivity.domain.model.Priority
import com.example.productivity.ui.theme.BackgroundDark
import com.example.productivity.ui.theme.PrimaryGreen
import com.example.productivity.ui.theme.PrimaryOrange
import com.example.productivity.ui.theme.PrimaryTeal
import com.example.productivity.ui.theme.SurfaceDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    onBackClick: () -> Unit,
    viewModel: CreateTaskViewModel = hiltViewModel()
) {
    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val priority by viewModel.priority.collectAsState()
    val dueDate by viewModel.dueDate.collectAsState()
    val effort by viewModel.estimatedEffort.collectAsState()
    val startTime by viewModel.startTime.collectAsState()

    // Date picker state
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dueDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
    
    // Time picker state
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = startTime.hour,
        initialMinute = startTime.minute,
        is24Hour = false
    )

    // Date formatter
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd MMM yyyy") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("h:mm a") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceDark,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                        }
                    }
                },
                actions = {
                    Button(
                        onClick = { 
                            viewModel.saveTask(onSuccess = onBackClick)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Done", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            
            // Task Title Input
            OutlinedTextField(
                value = title,
                onValueChange = viewModel::onTitleChange,
                textStyle = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = PrimaryOrange
                ),
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Task Title", style = MaterialTheme.typography.titleLarge, color = Color.Gray) }
            )

            Spacer(modifier = Modifier.height(24.dp))
            
            // Priority Selection
            Label("Priority level")
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PriorityChip(
                    text = "Low",
                    selected = priority == Priority.LOW,
                    color = PrimaryTeal,
                    onClick = { viewModel.onPriorityChange(Priority.LOW) }
                )
                PriorityChip(
                    text = "Medium",
                    selected = priority == Priority.MEDIUM,
                    color = PrimaryOrange,
                    onClick = { viewModel.onPriorityChange(Priority.MEDIUM) }
                )
                PriorityChip(
                    text = "High",
                    selected = priority == Priority.HIGH,
                    color = PrimaryGreen,
                    onClick = { viewModel.onPriorityChange(Priority.HIGH) }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Time Display (dynamic based on effort)
            Label("Time")
            val endTime = viewModel.getEndTime()
            val timeText = "${startTime.format(timeFormatter)} - ${endTime.format(timeFormatter)}"
            
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTimePicker = true },
                color = Color.Transparent
            ) {
                TaskOptionRow(icon = Icons.Default.Schedule, text = timeText)
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Effort Slider
            Label("Estimated Effort (Hours): $effort")
            Slider(
                value = effort.toFloat(),
                onValueChange = { viewModel.onEffortChange(it.toInt()) },
                valueRange = 1f..8f,
                steps = 6,
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryOrange,
                    activeTrackColor = PrimaryOrange
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            // Due Date with Calendar
            Label("Due date")
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                color = Color.Transparent
            ) {
                TaskOptionRow(
                    icon = Icons.Default.DateRange, 
                    text = dueDate.format(dateFormatter)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime()
                            viewModel.onDueDateChange(selectedDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = PrimaryOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = SurfaceDark
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = SurfaceDark,
                    titleContentColor = Color.White,
                    headlineContentColor = Color.White,
                    weekdayContentColor = Color.Gray,
                    subheadContentColor = Color.Gray,
                    dayContentColor = Color.White,
                    selectedDayContainerColor = PrimaryOrange,
                    selectedDayContentColor = Color.White,
                    todayContentColor = PrimaryOrange,
                    todayDateBorderColor = PrimaryOrange,
                    yearContentColor = Color.White,
                    currentYearContentColor = PrimaryOrange,
                    selectedYearContainerColor = PrimaryOrange,
                    selectedYearContentColor = Color.White
                )
            )
        }
    }

    // Time Picker Dialog
    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onStartTimeChange(java.time.LocalTime.of(timePickerState.hour, timePickerState.minute))
                        showTimePicker = false
                    }
                ) {
                    Text("OK", color = PrimaryOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        ) {
            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    clockDialColor = SurfaceDark,
                    selectorColor = PrimaryOrange,
                    containerColor = BackgroundDark,
                    clockDialSelectedContentColor = Color.White,
                    clockDialUnselectedContentColor = Color.Gray,
                    periodSelectorBorderColor = PrimaryOrange,
                    periodSelectorSelectedContainerColor = PrimaryOrange.copy(alpha = 0.3f),
                    periodSelectorSelectedContentColor = PrimaryOrange,
                    periodSelectorUnselectedContainerColor = Color.Transparent,
                    periodSelectorUnselectedContentColor = Color.Gray,
                    timeSelectorSelectedContainerColor = PrimaryOrange.copy(alpha = 0.3f),
                    timeSelectorSelectedContentColor = PrimaryOrange,
                    timeSelectorUnselectedContainerColor = SurfaceDark,
                    timeSelectorUnselectedContentColor = Color.White
                )
            )
        }
    }
}

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        text = content,
        containerColor = BackgroundDark
    )
}

@Composable
fun PriorityChip(
    text: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (selected) color else color.copy(alpha = 0.2f),
        contentColor = if (selected) Color.Black else color
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun Label(text: String) {
    Text(text, color = Color.Gray, fontSize = 14.sp)
}

@Composable
fun TaskFormField(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, color = Color.Gray, modifier = Modifier.width(100.dp))
        Text(value, color = Color.White, maxLines = 1)
    }
}

@Composable
fun TaskOptionRow(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}

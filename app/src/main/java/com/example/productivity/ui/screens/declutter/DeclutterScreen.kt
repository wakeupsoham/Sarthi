package com.example.productivity.ui.screens.declutter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.productivity.domain.model.Task
import com.example.productivity.ui.theme.BackgroundDark
import com.example.productivity.ui.theme.PrimaryOrange
import com.example.productivity.ui.theme.SurfaceDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeclutterScreen(
    onFinish: () -> Unit,
    viewModel: DeclutterViewModel = hiltViewModel()
) {
    val unfinishedTasks by viewModel.unfinishedTasks.collectAsState()
    val completedCount by viewModel.completedCount.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nightly Declutter", color = Color.White) },
                actions = {
                    IconButton(onClick = onFinish) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {
            Text(
                text = "Great job today!",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            Text(
                text = "You completed $completedCount tasks. Let's clear the rest.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (unfinishedTasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("All clear! Sleep well.", color = Color.White, fontWeight = FontWeight.Bold)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(unfinishedTasks) { task ->
                        DeclutterTaskItem(
                            task = task,
                            onMoveToTomorrow = { viewModel.moveToTomorrow(task) },
                            onReschedule = { viewModel.reschedule(task, 7) },
                            onDelete = { viewModel.deleteTask(task.id) }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onFinish,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("I'm Done", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DeclutterTaskItem(
    task: Task,
    onMoveToTomorrow: () -> Unit,
    onReschedule: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(task.title, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onMoveToTomorrow) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Update, contentDescription = "Tomorrow", tint = Color.White)
                        Text("+1d", fontSize = 10.sp, color = Color.Gray)
                    }
                }
                IconButton(onClick = onReschedule) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Event, contentDescription = "Weekend", tint = Color.White)
                        Text("+7d", fontSize = 10.sp, color = Color.Gray)
                    }
                }
                IconButton(onClick = onDelete) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f))
                        Text("Delete", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

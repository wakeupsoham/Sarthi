package com.example.productivity.ui.screens.focus

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.productivity.ui.theme.PrimaryOrange
import com.example.productivity.ui.theme.PrimaryGreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun FocusScreen(
    viewModel: FocusViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val timeLeft by viewModel.timeLeft.collectAsState()
    val currentTask by viewModel.currentTaskTitle.collectAsState()

    val totalTime = if (state == FocusState.BREAK_RUNNING) 5 * 60L else 25 * 60L
    val progress = timeLeft.toFloat() / totalTime.toFloat()
    
    val primaryColor by animateColorAsState(
        targetValue = if (state == FocusState.BREAK_RUNNING || state == FocusState.BREAK_PENDING) PrimaryGreen else PrimaryOrange
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Status Text
        Text(
            text = when (state) {
                FocusState.IDLE -> "Ready to Focus?"
                FocusState.RUNNING -> "Focusing..."
                FocusState.PAUSED -> "Paused"
                FocusState.BREAK_PENDING -> "Well Done!"
                FocusState.BREAK_RUNNING -> "Rest & Recharge"
            },
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
        
        if (currentTask != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(currentTask!!, color = Color.Gray)
        }
        
        Spacer(modifier = Modifier.height(48.dp))

        // Timer Dial
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = if (state == FocusState.RUNNING) 1.05f else 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "timerPulse"
        )

        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(250.dp)) {
                drawArc(
                    color = Color.DarkGray,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = primaryColor,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer(scaleX = pulseScale, scaleY = pulseScale)
            ) {
                val minutes = timeLeft / 60
                val seconds = timeLeft % 60
                Text(
                    text = "%02d:%02d".format(minutes, seconds),
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Controls
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            when (state) {
                FocusState.IDLE -> {
                    Button(
                        onClick = { viewModel.startFocus("General Study") }, // Default task for now
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        shape = CircleShape,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Start", modifier = Modifier.size(40.dp))
                    }
                }
                FocusState.RUNNING -> {
                     Button(
                        onClick = { viewModel.pauseTimer() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                        shape = CircleShape,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = "Pause", modifier = Modifier.size(40.dp))
                    }
                }
                FocusState.PAUSED -> {
                    Button(
                        onClick = { viewModel.stopSession() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha=0.2f)),
                        shape = CircleShape,
                        modifier = Modifier.size(64.dp)
                    ) {
                         Icon(Icons.Default.Stop, contentDescription = "Stop", tint = Color.Red)
                    }
                    Button(
                        onClick = { viewModel.resumeTimer() },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        shape = CircleShape,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Resume", modifier = Modifier.size(40.dp))
                    }
                }
                FocusState.BREAK_PENDING -> {
                     Button(
                        onClick = { viewModel.startBreak() },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(50.dp)
                    ) {
                        Text("Start Break (5m)", fontWeight = FontWeight.Bold)
                    }
                }
                FocusState.BREAK_RUNNING -> {
                     Button(
                        onClick = { viewModel.stopSession() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Skip Break")
                    }
                }
            }
        }
    }
}

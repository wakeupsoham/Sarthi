package com.example.productivity.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.productivity.ui.theme.PrimaryGreen
import com.example.productivity.ui.theme.PrimaryOrange

@Composable
fun WeeklyProgressScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Weekly Progress",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Chart Container
        Surface(
            modifier = Modifier.fillMaxWidth().height(300.dp),
            shape = RoundedCornerShape(32.dp),
            color = Color(0xFF1C1C1E) // Dark Card BG
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Productivity Flow", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.weight(1f))
                
                // Bar Chart
                Row(
                   modifier = Modifier.fillMaxWidth(),
                   horizontalArrangement = Arrangement.SpaceBetween,
                   verticalAlignment = Alignment.Bottom
                ) {
                    val data = listOf(
                        "Mon" to 0.4f,
                        "Tue" to 0.6f,
                        "Wed" to 0.8f,
                        "Thu" to 0.3f,
                        "Fri" to 0.5f,
                        "Sat" to 1.0f,
                        "Sun" to 0.2f
                    )
                    
                    data.forEach { (day, intensity) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .width(36.dp)
                                    .height((150 * intensity).dp) // simplistic scaling
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (intensity == 1.0f) PrimaryGreen else Color.DarkGray
                                    )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(day, color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Insight Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = PrimaryGreen
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("INSIGHT", fontSize = 10.sp, color = Color.Black.copy(alpha=0.6f), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "You completed 80% of planned work this week.",
                    color = Color.Black,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Quick Tip Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF26C6DA) // Cyan
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("QUICK TIP", fontSize = 10.sp, color = Color.Black.copy(alpha=0.6f), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Most delays happen in the evening. Try starting tasks 1hr earlier.",
                    color = Color.Black,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

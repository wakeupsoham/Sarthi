package com.example.productivity.ui.screens.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.productivity.domain.model.ProjectType
import com.example.productivity.ui.components.CalmPrimaryFilterChip
import com.example.productivity.ui.theme.BackgroundDark
import com.example.productivity.ui.theme.PrimaryOrange
import com.example.productivity.ui.theme.SurfaceDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectScreen(
    onBackClick: () -> Unit,
    viewModel: CreateProjectViewModel = hiltViewModel()
) {
    val name by viewModel.name.collectAsState()
    val type by viewModel.type.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Project", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    Button(
                        onClick = { viewModel.saveProject(onSuccess = onBackClick) }, 
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Create", color = Color.White, fontWeight = FontWeight.Bold)
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
            Text("What are you working on?", style = MaterialTheme.typography.titleLarge, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Project Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryOrange,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = PrimaryOrange
                )
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("Project Type", color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ProjectType.values().forEach { projectType ->
                    CalmPrimaryFilterChip(
                        text = projectType.name,
                        selected = type == projectType,
                        onClick = { viewModel.onTypeChange(projectType) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Mocking the AI suggestion part
            Surface(
                color = SurfaceDark,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("\u2728 AI Suggestion", color = PrimaryOrange, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Based on your project name, I suggest breaking this down into 'Research', 'Prototype', and 'Final Polish'.",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

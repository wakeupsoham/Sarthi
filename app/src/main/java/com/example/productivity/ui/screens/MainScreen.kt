package com.example.productivity.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.productivity.ui.navigation.Screen
import com.example.productivity.ui.screens.dashboard.DashboardScreen
import com.example.productivity.ui.screens.task.CreateTaskScreen
import com.example.productivity.ui.theme.BackgroundDark
import com.example.productivity.ui.theme.PrimaryOrange

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    
    val bottomNavItems = listOf(
        Screen.Dashboard to Icons.Default.Home,
        Screen.Calendar to Icons.Default.CalendarToday,
        Screen.Focus to Icons.Default.Timer,
        Screen.Stats to Icons.Default.BarChart
        // Screen.Profile to Icons.Default.Person // Placeholder
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            // Only show bottom bar on top-level screens
            val isTopLevel = bottomNavItems.any { it.first.route == currentDestination?.route }
            
            if (isTopLevel) {
                NavigationBar(
                    containerColor = BackgroundDark,
                    contentColor = Color.Gray
                ) {
                    bottomNavItems.forEach { (screen, icon) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = null) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryOrange,
                                unselectedIconColor = Color.Gray,
                                indicatorColor = Color.Transparent
                            ),
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController, 
            startDestination = Screen.Dashboard.route, 
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onFabClick = { navController.navigate(Screen.CreateTask.route) },
                    onDeclutterClick = { navController.navigate(Screen.Declutter.route) }
                )
            }
            composable(Screen.Stats.route) {
                com.example.productivity.ui.screens.stats.WeeklyProgressScreen()
            }
            composable(Screen.Calendar.route) {
                com.example.productivity.ui.screens.calendar.CalendarScreen()
            }
            composable(Screen.Focus.route) {
               com.example.productivity.ui.screens.focus.FocusScreen()
            }
            composable(Screen.CreateTask.route) {
                CreateTaskScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Screen.TaskList.route) {
                // Placeholder for TaskListScreen
                Text(text = "Task List Screen")
            }
            composable(Screen.CreateProject.route) {
                com.example.productivity.ui.screens.project.CreateProjectScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Screen.Declutter.route) {
                com.example.productivity.ui.screens.declutter.DeclutterScreen(
                    onFinish = { navController.popBackStack() }
                )
            }
        }
    }
}

package com.example.productivity.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.productivity.ui.screens.dashboard.DashboardScreen
import com.example.productivity.ui.screens.task.CreateTaskScreen
import com.example.productivity.ui.screens.auth.LoginScreen
import com.example.productivity.ui.screens.auth.SignUpScreen
import com.example.productivity.ui.screens.auth.AuthViewModel
import com.example.productivity.ui.navigation.Screen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.productivity.ui.screens.MainScreen

@Composable
fun MainNavigation(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val currentUser by authViewModel.currentUser.collectAsState()
    val isGuestMode by authViewModel.isGuestMode.collectAsState()

    // Fixed start destination to prevent NavHost reconstruction flakiness
    val startDestination = Screen.Login.route
    
    // Redirect if already logged in or in guest mode
    // We use a local state to ensure we don't trigger multiple navigations
    var hasRedirected by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    LaunchedEffect(currentUser, isGuestMode) {
        if (!hasRedirected && (currentUser != null || isGuestMode)) {
            hasRedirected = true
            navController.navigate("home_graph") {
                popUpTo(Screen.Login.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn(animationSpec = tween(500)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(500)) + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(500)) },
        popEnterTransition = { fadeIn(animationSpec = tween(500)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(500)) },
        popExitTransition = { fadeOut(animationSpec = tween(500)) + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(500)) }
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { /* Handled by LaunchedEffect above */ },
                onSignUpClick = { navController.navigate(Screen.SignUp.route) },
                viewModel = authViewModel
            )
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onBackClick = { navController.popBackStack() },
                onSignUpSuccess = { /* Handled by LaunchedEffect above */ },
                viewModel = authViewModel
            )
        }
        
        // The "Authenticated" section
        composable("home_graph") {
            com.example.productivity.ui.screens.MainScreen()
        }
    }
}

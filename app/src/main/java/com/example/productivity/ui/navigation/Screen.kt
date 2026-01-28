package com.example.productivity.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Dashboard : Screen("dashboard")
    object CreateTask : Screen("create_task")
    object TaskList : Screen("task_list")
    object Calendar : Screen("calendar")
    object Focus : Screen("focus")
    object Declutter : Screen("declutter")
    object CreateProject : Screen("create_project")
    object Stats : Screen("stats")
}

package com.windingmaster.app.presentation.navigation
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.windingmaster.app.presentation.screens.checklist.ChecklistScreen
import com.windingmaster.app.presentation.screens.experiences.ExperiencesScreen
import com.windingmaster.app.presentation.screens.motors.MotorsScreen
import com.windingmaster.app.presentation.screens.settings.SettingsScreen
sealed class Screen(val route: String, val title: String) {
    object Motors : Screen("motors", "موتورها")
    object Calculator : Screen("calculator", "محاسبات")
    object Checklist : Screen("checklist", "چک‌لیست")
    object Experiences : Screen("experiences", "تجربیات")
    object Settings : Screen("settings", "تنظیمات")
}
@Composable
fun MainNavigation(navController: NavHostController) {
    val items = listOf(Screen.Motors, Screen.Calculator, Screen.Checklist, Screen.Experiences, Screen.Settings)
    Scaffold(bottomBar = {
        NavigationBar {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            items.forEach { screen ->
                NavigationBarItem(
                    icon = { when (screen) {
                        Screen.Motors -> Icon(Icons.Default.ElectricBolt, null)
                        Screen.Calculator -> Icon(Icons.Default.Calculate, null)
                        Screen.Checklist -> Icon(Icons.Default.Checklist, null)
                        Screen.Experiences -> Icon(Icons.Default.Build, null)
                        Screen.Settings -> Icon(Icons.Default.Settings, null)
                    } },
                    label = { Text(screen.title) },
                    selected = currentRoute == screen.route,
                    onClick = { navController.navigate(screen.route) { popUpTo(navController.graph.startDestinationId) { saveState = true }; launchSingleTop = true; restoreState = true } }
                )
            }
        }
    }) { innerPadding ->
        NavHost(navController, startDestination = Screen.Motors.route, modifier = Modifier.padding(innerPadding)) {
            composable(Screen.Motors.route) { MotorsScreen() }
            composable(Screen.Calculator.route) { PlaceholderScreen("بخش محاسبات") }
            composable(Screen.Checklist.route) { ChecklistScreen() }
            composable(Screen.Experiences.route) { ExperiencesScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}

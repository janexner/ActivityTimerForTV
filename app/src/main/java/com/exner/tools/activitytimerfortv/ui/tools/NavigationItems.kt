package com.exner.tools.activitytimerfortv.ui.tools

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.exner.tools.activitytimerfortv.ui.destination.destinations.CategoryListDestination
import com.exner.tools.activitytimerfortv.ui.destination.destinations.HomeDestination
import com.exner.tools.activitytimerfortv.ui.destination.destinations.ProcessListDestination
import com.exner.tools.activitytimerfortv.ui.destination.destinations.SettingsDestination
import com.ramcosta.composedestinations.spec.Direction

// Create Navigation Items Class to Select UnSelect items
data class NavigationItems(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val destination: Direction,
)

val navigationItemAccount = NavigationItems(
    title = "Account",
    description = "",
    icon = Icons.Default.AccountCircle,
    ProcessListDestination
)

val navigationItems = listOf(
    NavigationItems(
        title = "Home",
        description = "Home",
        icon = Icons.Default.Home,
        HomeDestination
    ),
    NavigationItems(
        title = "Manage Processes",
        description = "Manage Processes",
        icon = Icons.Default.Edit,
        ProcessListDestination
    ),
    NavigationItems(
        title = "Manage Categories",
        description = "Manage categories",
        icon = Icons.Default.Edit,
        CategoryListDestination
    )
)

val navigationItemSettings = NavigationItems(
    title = "Settings",
    description = "App settings",
    icon = Icons.Default.Settings,
    SettingsDestination
)
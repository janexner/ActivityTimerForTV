package com.exner.tools.activitytimerfortv.ui.tools

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.ramcosta.composedestinations.generated.destinations.CategoryListDestination
import com.ramcosta.composedestinations.generated.destinations.ImportFromNearbyDeviceDestination
import com.ramcosta.composedestinations.generated.destinations.ProcessListDestination
import com.ramcosta.composedestinations.generated.destinations.SettingsDestination
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
    destination = ProcessListDestination
)

val navigationItems = listOf(
    NavigationItems(
        title = "Manage Processes",
        description = "Manage Processes",
        icon = Icons.Default.Edit,
        destination = ProcessListDestination
    ),
    NavigationItems(
        title = "Import from nearby",
        description = "Import Processes from nearby device",
        icon = Icons.Default.AddCircle,
        destination = ImportFromNearbyDeviceDestination
    ),
//    NavigationItems(
//        title = "Import Processes (Web)",
//        description = "Import Processes from the web",
//        icon = Icons.Default.AddCircle,
//        destination = RemoteProcessManagementDestination
//    ),
    NavigationItems(
        title = "Manage Categories",
        description = "Manage categories",
        icon = Icons.Default.Edit,
        destination = CategoryListDestination
    )
)

val navigationItemSettings = NavigationItems(
    title = "Settings",
    description = "App settings",
    icon = Icons.Default.Settings,
    SettingsDestination
)
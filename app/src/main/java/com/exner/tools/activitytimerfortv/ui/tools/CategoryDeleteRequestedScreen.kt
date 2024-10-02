package com.exner.tools.activitytimerfortv.ui.tools

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.exner.tools.activitytimerfortv.data.persistence.TimerCategoryIdNameCount

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class,
    ExperimentalTvMaterial3Api::class
)
@Composable
fun CategoryDeleteRequestedScreen(
    openDeleteDialog: Boolean,
    categoryName: String,
    categoryUsage: TimerCategoryIdNameCount?,
    confirmCallback: () -> Unit,
    dismissCallback: () -> Unit
) {
    StandardDialog(
        showDialog = openDeleteDialog,
        title = { Text(text = "Delete Category $categoryName?") },
        text = {
            if (categoryUsage != null) {
                Text(text = "$categoryName is used by ${categoryUsage.usageCount} processes! Really want to delete it?")
            } else {
                Text(text = "Confirm that you want to delete $categoryName")
            }
        },
        icon = { Icon(imageVector = Icons.Default.Warning, contentDescription = "Alert") },
        onDismissRequest = { dismissCallback() },
        confirmButton = {
            Button(onClick = { confirmCallback() }) {
                Text(text = "Delete")
            }
        },
        dismissButton = {
            Button(onClick = { dismissCallback() }) {
                Text(text = "Cancel")
            }
        }
    )
}
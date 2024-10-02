package com.exner.tools.activitytimerfortv.ui.tools

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.exner.tools.activitytimerfortv.ui.DefaultSpacer
import com.exner.tools.activitytimerfortv.ui.destination.ProcessDeleteChainWarning

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class,
    ExperimentalTvMaterial3Api::class
)
@Composable
fun ProcessDeleteRequestedScreen(
    openDeleteDialog: Boolean,
    processName: String,
    processChainWarning: ProcessDeleteChainWarning?,
    confirmCallback: () -> Unit,
    dismissCallback: () -> Unit
) {
    if (openDeleteDialog) {
        StandardDialog(
            showDialog = openDeleteDialog,
            title = { Text(text = "Delete process $processName?") },
            text = {
                if (processChainWarning != null) {
                    Column {
                        DefaultSpacer()
                        Text(text = processChainWarning.title)
                        DefaultSpacer()
                        processChainWarning.processNames.forEach {
                            Text(text = it.name)
                        }
                        DefaultSpacer()
                        Text(text = processChainWarning.explanation)
                    }
                } else {
                    Text(text = "Confirm that you want to delete $processName")
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
}
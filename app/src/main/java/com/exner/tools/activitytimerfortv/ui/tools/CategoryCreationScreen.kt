package com.exner.tools.activitytimerfortv.ui.tools

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.TextStyle
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class,
    ExperimentalTvMaterial3Api::class
)
@Composable
fun CategoryCreationScreen(
    openCreateDialog: Boolean,
    confirmCallback: (name: String) -> Unit,
    dismissCallback: () -> Unit
) {
    val newCategoryName = remember { mutableStateOf("New Category") }

    StandardDialog(
        showDialog = openCreateDialog,
        title = { Text(text = "Create Category?") },
        text = {
            OutlinedTextField(
                value = newCategoryName.value,
                onValueChange = { newCategoryName.value = it },
                label = { Text(text = "Category name") },
                singleLine = true,
                textStyle = TextStyle(color = MaterialTheme.colorScheme.primary),
            )
        },
        icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add") },
        onDismissRequest = { dismissCallback() },
        confirmButton = {
            Button(onClick = { confirmCallback(newCategoryName.value) }) {
                Text(text = "Create")
            }
        },
        dismissButton = {
            Button(onClick = { dismissCallback() }) {
                Text(text = "Cancel")
            }
        }
    )
}
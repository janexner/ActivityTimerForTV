package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.exner.tools.activitytimerfortv.ui.CategoryEditViewModel
import com.exner.tools.activitytimerfortv.ui.tools.DefaultSpacer
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@Destination<RootGraph>
@Composable
fun CategoryEdit(
    categoryUid: Long,
    categoryEditViewModel: CategoryEditViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {

    val name by categoryEditViewModel.name.observeAsState()
    val backgroundUri by categoryEditViewModel.backgroundUri.observeAsState()
    val usage by categoryEditViewModel.usage.observeAsState()

    categoryEditViewModel.getCategory(categoryUid)

    // remember values for the editable fields
    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = backgroundUri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(MaterialTheme.colorScheme.background, Color.Transparent)
                    )
                )
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 48.dp, vertical = 24.dp)
            ) {
                // buttons
                Row {
                    Button(
                        onClick = {
                            // TODO save edits
                            navigator.navigateUp()
                        },
                        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Done",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = "Done")
                    }
                }
                // spacer
                Spacer(modifier = Modifier.weight(0.1f))
                // content
                // more process information
                Column {
                    OutlinedTextField(
                        value = name ?: "",
                        onValueChange = { categoryEditViewModel.setName(it) },
                        label = { Text(text = "Category name") },
                        singleLine = true,
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.primary),
                    )
                    var usageString = "Unused"
                    if (usage != null) {
                        usageString = "Used in ${usage!!.usageCount} processes"
                    }
                    DefaultSpacer()
                    Text(text = usageString)
                    DefaultSpacer()

                    OutlinedTextField(
                        value = backgroundUri ?: "",
                        onValueChange = { categoryEditViewModel.setBackgroundUri(it) },
                        label = { Text(text = "Background URI") },
                        singleLine = true,
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.primary),
                    )
                }
            }
        }
    }
}
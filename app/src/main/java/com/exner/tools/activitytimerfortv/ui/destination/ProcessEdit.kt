package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.exner.tools.activitytimerfortv.ui.ProcessEditViewModel
import com.exner.tools.activitytimerfortv.ui.tools.DefaultSpacer
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@Destination<RootGraph>
@Composable
fun ProcessEdit(
    processUuid: String,
    processEditViewModel: ProcessEditViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {

    val name by processEditViewModel.name.observeAsState()
    val info by processEditViewModel.info.observeAsState()
    val processTime by processEditViewModel.processTime.observeAsState()
    val intervalTime by processEditViewModel.intervalTime.observeAsState()
    val hasAutoChain by processEditViewModel.hasAutoChain.observeAsState()
    val gotoId by processEditViewModel.gotoUuid.observeAsState()
    val gotoName by processEditViewModel.gotoName.observeAsState()
    val backgroundUri by processEditViewModel.backgroundUri.observeAsState()
    val categoryId by processEditViewModel.categoryId.observeAsState()

    processEditViewModel.getProcess(processUuid)

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = backgroundUri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier.background(
                Brush.linearGradient(
                    listOf(MaterialTheme.colorScheme.background, Color.Transparent)
                )
            ).fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 48.dp, vertical = 24.dp)
            ) {
                TopButtons(navigator)
                DefaultSpacer()
                Content(name, hasAutoChain, gotoName, backgroundUri)
            }
        }
    }
}

@Composable
private fun Content(
    name: String?,
    hasAutoChain: Boolean?,
    gotoName: String?,
    backgroundUri: String?
) {
    // content
    Box(
        modifier = Modifier.fillMaxHeight(0.9f)
    ) {
        // TODO
    }
    // spacer
    DefaultSpacer()
    // more process information
    Row {
        val tempName: String = name ?: ""
        Text(text = "Process: '$tempName'")
        if (hasAutoChain == true) {
            Text(text = ", when complete, will lead into '$gotoName'")
        }
    }
    DefaultSpacer()
    Text(text = "Background image URL: $backgroundUri")
}

@Composable
private fun TopButtons(navigator: DestinationsNavigator) {
    // buttons
    Row {
        Button(
            onClick = {
//                        navigator.navigate(ProcessEditDestination(processUuid = processUuid))
            },
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
        ) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = "Save",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "Save")
        }
        Spacer(modifier = Modifier.weight(0.5f))
        OutlinedButton(
            onClick = {
                navigator.navigateUp()
            },
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Cancel"
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "Cancel")
        }
    }
}

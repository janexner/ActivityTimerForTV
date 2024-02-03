package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.Text
import androidx.tv.material3.WideButton
import com.exner.tools.activitytimerfortv.ui.ProcessDetailsViewModel
import com.exner.tools.activitytimerfortv.ui.destination.destinations.ProcessDeleteDestination
import com.exner.tools.activitytimerfortv.ui.destination.destinations.ProcessRunDestination
import com.exner.tools.activitytimerfortv.ui.destination.destinations.SettingsDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@OptIn(ExperimentalTvMaterial3Api::class)
@Destination
@Composable
fun ProcessDetails(
    processUuid: String,
    processDetailsViewModel: ProcessDetailsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {

    val name by processDetailsViewModel.name.observeAsState()
    val processTime by processDetailsViewModel.processTime.observeAsState()
    val intervalTime by processDetailsViewModel.intervalTime.observeAsState()
    val hasAutoChain by processDetailsViewModel.hasAutoChain.observeAsState()
    val gotoId by processDetailsViewModel.gotoUuid.observeAsState()
    val gotoName by processDetailsViewModel.gotoName.observeAsState()

    processDetailsViewModel.getProcess(processUuid)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row {
            WideButton(
                onClick = {
                    navigator.navigate(
                        ProcessRunDestination(processUuid = processUuid)
                    )
                },
                title = { Text(text = "Start Process") },
                icon = { Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "Start Process") }
            )
            WideButton(
                onClick = {
                    navigator.navigate(ProcessDeleteDestination(processUuid = processUuid))
                },
                title = { Text(text = "Delete Process") },
                icon = { Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete Process") }
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    navigator.navigate(SettingsDestination)
                },
            ) {
                Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = name ?: "Name")
        HorizontalDivider()
        Text(text = "Runs $processTime seconds with $intervalTime intervals.")
    }
}
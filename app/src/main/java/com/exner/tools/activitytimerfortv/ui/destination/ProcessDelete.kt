package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.Text
import com.exner.tools.activitytimerfortv.ui.HeaderText
import com.exner.tools.activitytimerfortv.ui.ProcessDeleteViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ProcessListDestination
import com.ramcosta.composedestinations.generated.destinations.SettingsDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<RootGraph>
@Composable
fun ProcessDelete(
    processUuid: String,
    processDeleteViewModel: ProcessDeleteViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {

    val processName by processDeleteViewModel.processName.observeAsState()
    val processIsTarget by processDeleteViewModel.processIsTarget.observeAsState()
    val dependantProcesses by processDeleteViewModel.processChainingDependencies.observeAsState()

    processDeleteViewModel.checkProcess(processUuid)

    Column(
        modifier = Modifier.padding(24.dp)
    ) {
        Row {
            Button(
                onClick = {
                    navigator.navigateUp()
                },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding
            ) {
                Icon(
                    imageVector = Icons.Filled.Close, contentDescription = "Cancel",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = "Cancel")
            }
            Spacer(modifier = Modifier.size(8.dp))
            Button(
                onClick = {
                    processDeleteViewModel.deleteProcess(processUuid)
                    navigator.navigate(ProcessListDestination)
                },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete Process",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = "Delete Process")
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    navigator.navigate(SettingsDestination)
                },
            ) {
                Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings")
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        HeaderText(text = "Delete Process")
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "You are about to delete a process,"
        )
        Text(text = "'$processName'.")
        // double-check in case there are chains that contain this
        if (processIsTarget == true) {
            if (dependantProcesses != null && dependantProcesses!!.dependentProcessIdsAndNames.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Other processes link to this one!")
                Spacer(modifier = Modifier.height(8.dp))
                if (dependantProcesses!!.dependentProcessIdsAndNames.isNotEmpty()) {
                    dependantProcesses!!.dependentProcessIdsAndNames.forEach {
                        Text(text = it.name)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "If you delete this process, those others will no longer be able to link to it, meaning they will stop when they try to.")
            }
        }
    }
}


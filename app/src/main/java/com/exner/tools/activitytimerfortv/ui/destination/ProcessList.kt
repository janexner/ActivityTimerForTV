package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.material3.ClassicCard
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.Text
import androidx.tv.material3.WideButton
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcess
import com.exner.tools.activitytimerfortv.ui.ProcessListViewModel
import com.exner.tools.activitytimerfortv.ui.destination.destinations.ProcessDetailsDestination
import com.exner.tools.activitytimerfortv.ui.destination.destinations.RemoteProcessManagementDestination
import com.exner.tools.activitytimerfortv.ui.destination.destinations.SettingsDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalTvMaterial3Api::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun ProcessList(
    processListViewModel: ProcessListViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {

    val processes: List<TimerProcess> by processListViewModel.observeProcessesRaw.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    Column(
        modifier = Modifier.padding(24.dp)
    ) {
        Row {
            WideButton(
                onClick = { /*TODO*/ },
                title = { Text(text = "Add Process") },
                icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Process") }
            )
            Spacer(modifier = Modifier.size(8.dp))
            WideButton(
                onClick = {
                    navigator.navigate(RemoteProcessManagementDestination)
                },
                title = { Text(text = "Import Processes") },
                icon = { Icon(imageVector = Icons.Filled.AddCircle, contentDescription = "Import Processes") }
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
        TvLazyVerticalGrid(columns = TvGridCells.Adaptive(minSize = 250.dp)) {
            items(processes.size) { index ->
                val process = processes[index]
                val infoText = process.info + if (process.hasAutoChain) " > ${process.gotoName}" else ""
                ClassicCard(
                    modifier = Modifier.padding(8.dp),
                    onClick = {
                        navigator.navigate(
                            ProcessDetailsDestination(
                                processUuid = process.uuid
                            )
                        )
                    },
                    contentPadding = PaddingValues(8.dp),
                    title = {
                        Text(text = process.name)
                    },
                    subtitle = {
                        Text(
                            text = "${process.processTime} / ${process.intervalTime}",
                        )
                    },
                    description = { Text(text = infoText) },
                    image = {}
                )
            }
        }
    }
}

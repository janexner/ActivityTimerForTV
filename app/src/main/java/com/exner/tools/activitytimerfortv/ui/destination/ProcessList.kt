package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.ClassicCard
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.Text
import androidx.tv.material3.WideButton
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcess
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcessCategory
import com.exner.tools.activitytimerfortv.ui.ProcessListViewModel
import com.exner.tools.activitytimerfortv.ui.tools.ActivityTimerNavigationDrawerContent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.CategoryListDestination
import com.ramcosta.composedestinations.generated.destinations.ProcessDetailsDestination
import com.ramcosta.composedestinations.generated.destinations.RemoteProcessManagementDestination
import com.ramcosta.composedestinations.generated.destinations.SettingsDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<RootGraph>
@Composable
fun ProcessList(
    processListViewModel: ProcessListViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {

    val processes: List<TimerProcess> by processListViewModel.observeProcessesRaw.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )
    val categories: List<TimerProcessCategory> by processListViewModel.observeCategoriesRaw.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    var modified by remember { mutableStateOf(false) }

    NavigationDrawer(drawerContent = {
        ActivityTimerNavigationDrawerContent(
            navigator = navigator,
            defaultSelectedIndex = 2, // this should not be a constant!
        )
    }) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row {
                WideButton(
                    onClick = { /*TODO*/ },
                    title = { Text(text = "Add Process") },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Process"
                        )
                    }
                )
                Spacer(modifier = Modifier.size(8.dp))
                WideButton(
                    onClick = {
                        navigator.navigate(RemoteProcessManagementDestination)
                    },
                    title = { Text(text = "Import Processes") },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.AddCircle,
                            contentDescription = "Import Processes"
                        )
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
                WideButton(
                    onClick = {
                        navigator.navigate(CategoryListDestination)
                    },
                    title = { Text(text = "Manage Categories") },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Import Processes"
                        )
                    }
                )
                Spacer(modifier = Modifier.size(8.dp))
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
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 250.dp)) {
                    items(processes.size) { index ->
                        val process = processes[index]
                        val infoText =
                            process.info + if (process.hasAutoChain) " > ${process.gotoName}" else ""
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
    }
}

package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.rememberTvLazyGridState
import androidx.tv.material3.Checkbox
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.ListItem
import androidx.tv.material3.Surface
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import androidx.tv.material3.WideButton
import com.exner.tools.activitytimerfortv.ui.BodyText
import com.exner.tools.activitytimerfortv.ui.HeaderText
import com.exner.tools.activitytimerfortv.ui.RemoteProcessManagementViewModel
import com.exner.tools.activitytimerfortv.ui.SettingsViewModel
import com.exner.tools.activitytimerfortv.ui.destination.destinations.SettingsDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

sealed class ProcessListTabs(val name: String) {
    data object RemoteOnlyTab : ProcessListTabs("Remote")
}

@Destination
@Composable
fun RemoteProcessManagement(
    remoteProcessManagementViewModel: RemoteProcessManagementViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {

//    val listStateLocal = rememberLazyListState()
//    val localProcesses by processListViewModel.observeProcessesForCurrentCategory.collectAsStateWithLifecycle()
//
//    val listOfProcessIdsToUpload = remember {
//        mutableStateListOf<String>()
//    }

    val loadingRemote = remember { mutableStateOf(false) }
    val listStateRemote = rememberTvLazyGridState()
    val remoteProcesses by remoteProcessManagementViewModel.remoteProcessesRaw.collectAsStateWithLifecycle()

    val listOfProcessUuidsToImport = remember {
        mutableStateListOf<String>()
    }

    val importAndUploadRestOfChainAutomatically by settingsViewModel.importAndUploadRestOfChainAutomatically.collectAsStateWithLifecycle()

    var tabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabItems = listOf(ProcessListTabs.RemoteOnlyTab)

    remoteProcessManagementViewModel.loadRemoteProcesses()

    val openAlertDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(24.dp)
    ) {
        Row {
            if (tabIndex == 0 && listOfProcessUuidsToImport.size > 0) {
                WideButton(
                    onClick = {
                        openAlertDialog.value = true
                    },
                    title = { Text(text = "Import Processes") },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Import Processes"
                        )
                    }
                )
            }
            WideButton(
                onClick = {
                    navigator.navigateUp()
                },
                title = { Text(text = "Cancel") },
                icon = { Icon(imageVector = Icons.Filled.Close, contentDescription = "Cancel") }
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
        TabRow(
            selectedTabIndex = tabIndex,
        ) {
            tabItems.forEachIndexed { index, settingsTabs ->
                Tab(
                    selected = index == tabIndex,
                    onClick = { tabIndex = index },
                    onFocus = {},
                    content = { BodyText(text = settingsTabs.name) }
                )
            }
        }
        when (tabIndex) {
            0 -> Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Tick processes you want to import, then use the 'Import Processes' button to import them.",
                    modifier = Modifier.padding(8.dp, 0.dp)
                )
//                        Spacer(modifier = Modifier.size(8.dp))
                TvLazyVerticalGrid(
                    columns = TvGridCells.Adaptive(minSize = 250.dp),
                    state = listStateRemote,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(remoteProcesses.size) { index ->
                        val genericProcess = remoteProcesses[index]
                        Surface {
                            ListItem(
                                selected = false,
                                onClick = {
                                    if (listOfProcessUuidsToImport.contains(genericProcess.uuid)) {
                                        listOfProcessUuidsToImport.remove(genericProcess.uuid)
                                    } else {
                                        listOfProcessUuidsToImport.add(genericProcess.uuid)
                                    }
                                },
                                leadingContent = {
                                    Checkbox(
                                        checked = listOfProcessUuidsToImport.contains(
                                            genericProcess.uuid
                                        ),
                                        onCheckedChange = { checked ->
                                            if (checked) {
                                                listOfProcessUuidsToImport.add(
                                                    genericProcess.uuid
                                                )
                                            } else {
                                                listOfProcessUuidsToImport.remove(
                                                    genericProcess.uuid
                                                )
                                            }
                                        })
                                },
                                headlineContent = {
                                    HeaderText(text = genericProcess.name)
                                },
                                supportingContent = {
                                    val nextOrNotText =
                                        if (null != genericProcess.gotoName) " > ${genericProcess.gotoName}" else ""
                                    BodyText(text = "${genericProcess.processTime} / ${genericProcess.intervalTime}$nextOrNotText")
                                }
                            )
                        }
                    }

                    // load indicator
                    item {
                        if (loadingRemote.value) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(width = 50.dp, height = 50.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    }
                }
            }
        }
        // Alert Dialog
        if (openAlertDialog.value) {
            val andDependents =
                if (importAndUploadRestOfChainAutomatically) " plus those started by them" else ""
            AlertDialog(
                icon = {},
                title = { Text(text = "Import?") },
                text = { Text(text = "Import ${listOfProcessUuidsToImport.size} process(es)$andDependents?") },
                onDismissRequest = { openAlertDialog.value = false },
                dismissButton = {
                    TextButton(onClick = {
                        openAlertDialog.value = false
                    }) {
                        Text(text = "No")
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        remoteProcessManagementViewModel.importProcessesFromRemote(
                            listOfProcessUuidsToImport,
                            importAndUploadRestOfChainAutomatically
                        )
                        openAlertDialog.value = false
                        navigator.navigateUp()
                    }) {
                        Text(text = "Yes, import")
                    }
                }
            )
        }
    } // end Column
}

package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.material3.ClassicCard
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcess
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcessCategory
import com.exner.tools.activitytimerfortv.ui.ProcessListViewModel
import com.exner.tools.activitytimerfortv.ui.destination.destinations.ProcessDetailsDestination
import com.exner.tools.activitytimerfortv.ui.tools.ActivityTimerNavigationDrawerContent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun Home(
    processListViewModel: ProcessListViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {

    val processes: List<TimerProcess> by processListViewModel.observeProcessesRaw.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )
    val currentCategory: TimerProcessCategory by processListViewModel.currentCategory.collectAsStateWithLifecycle(
        initialValue = TimerProcessCategory("All", -1L)
    )
    val categories: List<TimerProcessCategory> by processListViewModel.observeCategoriesRaw.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    NavigationDrawer(drawerContent = {
        ActivityTimerNavigationDrawerContent(
            navigator = navigator,
            defaultSelectedIndex = 1
        )
    }) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Tab(
                        selected = 0 == selectedTabIndex,
                        onFocus = { selectedTabIndex = 0 }
                    ) {
                        Text(
                            text = "All",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                    categories.forEachIndexed() { index, category ->
                        Tab(
                            selected = (index + 1) == selectedTabIndex, // the +1 is a hack for the "All" category
                            onFocus = { selectedTabIndex = index + 1 }
                        ) {
                            Text(
                                text = category.name,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                    }
                    Tab(
                        selected = (1 + categories.size) == selectedTabIndex,
                        onFocus = { selectedTabIndex = 1 + categories.size }
                    ) {
                        Text(
                            text = "None",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }

                TvLazyVerticalGrid(columns = TvGridCells.Adaptive(minSize = 250.dp)) {
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

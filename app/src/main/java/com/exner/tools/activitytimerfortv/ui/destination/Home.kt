package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.material3.ClassicCard
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcess
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcessCategory
import com.exner.tools.activitytimerfortv.ui.ProcessListViewModel
import com.exner.tools.activitytimerfortv.ui.TimerDisplay
import com.exner.tools.activitytimerfortv.ui.destination.destinations.ProcessDetailsDestination
import com.exner.tools.activitytimerfortv.ui.tools.ActivityTimerNavigationDrawerContent
import com.exner.tools.activitytimerfortv.ui.tools.CategoryListDefinitions
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlin.time.DurationUnit
import kotlin.time.toDuration

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
    val categories: List<TimerProcessCategory> by processListViewModel.observeCategoriesRaw.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    var selectedCategory by remember { mutableLongStateOf(-2L) }

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
                        .padding(top = 32.dp, bottom = 16.dp),
                ) {
                    Tab(
                        selected = 0 == selectedTabIndex,
                        onFocus = {
                            selectedCategory = CategoryListDefinitions.CATEGORY_UID_ALL
                            selectedTabIndex = 0
                        }
                    ) {
                        Text(
                            text = "View All",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    categories.forEachIndexed { index, category ->
                        Tab(
                            selected = (index + 1) == selectedTabIndex, // the +1 is a hack for the "All" category
                            onFocus = {
                                selectedCategory = category.uid
                                selectedTabIndex = index + 1
                            }
                        ) {
                            Text(
                                text = category.name,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                    Tab(
                        selected = (1 + categories.size) == selectedTabIndex,
                        onFocus = {
                            selectedCategory = CategoryListDefinitions.CATEGORY_UID_NONE
                            selectedTabIndex = 1 + categories.size
                        }
                    ) {
                        Text(
                            text = "None",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }

                val filteredProcesses = when (selectedCategory) {
                    CategoryListDefinitions.CATEGORY_UID_ALL -> {
                        processes
                    }
                    CategoryListDefinitions.CATEGORY_UID_NONE -> {
                        processes.filter { process ->
                            CategoryListDefinitions.CATEGORY_UID_NONE == process.categoryId || 0L == process.categoryId || null == process.categoryId
                        }
                    }
                    else -> {
                        processes.filter { process ->
                            selectedCategory == process.categoryId
                        }
                    }
                }
                TvLazyVerticalGrid(columns = TvGridCells.Adaptive(minSize = 250.dp)) {
                    items(filteredProcesses.size) { index ->
                        val process = filteredProcesses[index]
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
                            image = {
                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1.77f)
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(
                                                    MaterialTheme.colorScheme.borderVariant,
                                                    MaterialTheme.colorScheme.tertiaryContainer
                                                )
                                            )
                                        )
                                ) {
                                    TimerDisplay(
                                        processTime = process.processTime.toDuration(DurationUnit.SECONDS),
                                        intervalTime = process.intervalTime.toDuration(DurationUnit.SECONDS),
                                        info = process.info,
                                        forceWithHours = true
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

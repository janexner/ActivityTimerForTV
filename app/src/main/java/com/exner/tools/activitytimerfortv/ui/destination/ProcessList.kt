package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
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
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcess
import com.exner.tools.activitytimerfortv.ui.ProcessListViewModel
import com.exner.tools.activitytimerfortv.ui.destination.destinations.ProcessDetailsDestination
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
            Button(onClick = { /*TODO*/ }) {
                Text(text = "+ Add Process")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))
        TvLazyVerticalGrid(columns = TvGridCells.Adaptive(minSize = 250.dp)) {
            items(processes.size) { index ->
                val process = processes[index]
                ClassicCard(
                    modifier = Modifier.padding(8.dp),
                    onClick = {
                        navigator.navigate(
                            ProcessDetailsDestination(
                                processId = process.uid
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
                    description = { Text(text = process.info) },
                    image = {}
                )
            }
        }
    }
}

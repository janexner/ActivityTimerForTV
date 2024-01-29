package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcess
import com.exner.tools.activitytimerfortv.ui.ProcessListViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import androidx.compose.ui.unit.dp

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
    
    TvLazyVerticalGrid(columns = TvGridCells.Adaptive(minSize = 250.dp)) {
        items(processes.size) { index ->
            val process = processes[index]
            Card(onClick = { /*TODO*/ }) {
                Text(text = process.name)
            }
        }
    }
}
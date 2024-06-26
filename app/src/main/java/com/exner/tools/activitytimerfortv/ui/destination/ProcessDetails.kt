package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import androidx.tv.material3.WideButton
import com.exner.tools.activitytimerfortv.ui.ProcessDetailsViewModel
import com.exner.tools.activitytimerfortv.ui.TimerDisplay
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ProcessDeleteDestination
import com.ramcosta.composedestinations.generated.destinations.ProcessRunDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlin.time.DurationUnit
import kotlin.time.toDuration


@Destination<RootGraph>
@Composable
fun ProcessDetails(
    processUuid: String,
    processDetailsViewModel: ProcessDetailsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {

    val name by processDetailsViewModel.name.observeAsState()
    val info by processDetailsViewModel.info.observeAsState()
    val processTime by processDetailsViewModel.processTime.observeAsState()
    val intervalTime by processDetailsViewModel.intervalTime.observeAsState()
    val hasAutoChain by processDetailsViewModel.hasAutoChain.observeAsState()
    val gotoId by processDetailsViewModel.gotoUuid.observeAsState()
    val gotoName by processDetailsViewModel.gotoName.observeAsState()

    processDetailsViewModel.getProcess(processUuid)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp, 12.dp)
    ) {
        // buttons
        Row {
            WideButton(
                onClick = {
                    navigator.navigate(
                        ProcessRunDestination(processUuid = processUuid)
                    )
                },
                title = { Text(text = "Start Process") },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Start Process"
                    )
                }
            )
            Spacer(modifier = Modifier.size(8.dp))
            WideButton(
                onClick = {
//                        navigator.navigate(ProcessEditDestination(processUuid = processUuid))
                },
                title = { Text(text = "Edit Process") },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Process"
                    )
                }
            )
            Spacer(modifier = Modifier.weight(0.5f))
            WideButton(
                onClick = {
                    navigator.navigate(ProcessDeleteDestination(processUuid = processUuid))
                },
                title = { Text(text = "Delete Process") },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Process"
                    )
                }
            )
        }
        // spacer
        Spacer(modifier = Modifier.weight(0.1f))
        // content
        Box(
            modifier = Modifier.fillMaxHeight(0.9f)
        ) {
            if (null !== processTime && null !== intervalTime) {
                TimerDisplay(
                    processTime = processTime!!.toDuration(DurationUnit.SECONDS),
                    intervalTime = intervalTime!!.toDuration(DurationUnit.SECONDS),
                    info = info,
                    forceWithHours = true
                )
            }
        }
        // spacer
        Spacer(modifier = Modifier.size(16.dp))
        // more process information
        Row {
            val tempName: String = name ?: ""
            Text(text = "Process: '$tempName'")
            if (hasAutoChain == true) {
                Text(text = ", when complete, will lead into '$gotoName'")
            }
        }
    }
}
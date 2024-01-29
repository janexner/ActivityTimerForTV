package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.exner.tools.activitytimerfortv.ui.ProcessDetailsViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.exner.tools.activitytimerfortv.ui.destination.destinations.ProcessRunDestination


@OptIn(ExperimentalTvMaterial3Api::class)
@Destination
@Composable
fun ProcessDetails(
    processId: Long,
    processDetailsViewModel: ProcessDetailsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {

    val name by processDetailsViewModel.name.observeAsState()
    val processTime by processDetailsViewModel.processTime.observeAsState()
    val intervalTime by processDetailsViewModel.intervalTime.observeAsState()
    val hasAutoChain by processDetailsViewModel.hasAutoChain.observeAsState()
    val gotoId by processDetailsViewModel.gotoId.observeAsState()
    // this one is the odd one out
    val nextProcessesName by processDetailsViewModel.nextProcessesName.observeAsState()

    processDetailsViewModel.getProcess(processId)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row {
            Button(onClick = {
                navigator.navigate(
                    ProcessRunDestination(processId = processId)
                )
            }) {
                Text(text = "> Start Process")
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
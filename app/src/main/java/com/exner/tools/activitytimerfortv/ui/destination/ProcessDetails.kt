package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.exner.tools.activitytimerfortv.ui.ProcessDetailsViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import androidx.compose.runtime.livedata.observeAsState


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


}
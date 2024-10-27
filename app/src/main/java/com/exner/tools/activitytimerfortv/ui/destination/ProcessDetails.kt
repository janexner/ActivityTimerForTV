package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.exner.tools.activitytimerfortv.data.persistence.TimerDataIdAndName
import com.exner.tools.activitytimerfortv.ui.ProcessDetailsViewModel
import com.exner.tools.activitytimerfortv.ui.tools.AutoSizeText
import com.exner.tools.activitytimerfortv.ui.tools.BigTimerText
import com.exner.tools.activitytimerfortv.ui.tools.DefaultSpacer
import com.exner.tools.activitytimerfortv.ui.tools.InfoText
import com.exner.tools.activitytimerfortv.ui.tools.ProcessDeleteRequestedScreen
import com.exner.tools.activitytimerfortv.ui.tools.StandardButton
import com.exner.tools.activitytimerfortv.ui.tools.StandardOutlinedButton
import com.exner.tools.activitytimerfortv.ui.tools.durationToAnnotatedString
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ProcessEditDestination
import com.ramcosta.composedestinations.generated.destinations.ProcessRunDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlin.math.roundToInt
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class ProcessDeleteChainWarning(
    val title: String,
    val processNames: List<TimerDataIdAndName>,
    val explanation: String,
)

@Destination<RootGraph>
@Composable
fun ProcessDetails(
    processUuid: String,
    navigator: DestinationsNavigator
) {
    val processDetailsViewModel = hiltViewModel<ProcessDetailsViewModel, ProcessDetailsViewModel.ProcessDetailsViewModelFactory> { factory ->
        factory.create(processUuid)
    }

    val name by processDetailsViewModel.name.observeAsState()
    val info by processDetailsViewModel.info.observeAsState()
    val processTime by processDetailsViewModel.processTime.observeAsState()
    val intervalTime by processDetailsViewModel.intervalTime.observeAsState()
    val hasAutoChain by processDetailsViewModel.hasAutoChain.observeAsState()
    val gotoName by processDetailsViewModel.gotoName.observeAsState()
    val backgroundUri by processDetailsViewModel.backgroundUri.observeAsState()

    val processIsTarget by processDetailsViewModel.processIsTarget.observeAsState()
    val dependantProcesses by processDetailsViewModel.processChainingDependencies.observeAsState()

    val openDeletionDialog = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = backgroundUri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(MaterialTheme.colorScheme.background, Color.Transparent)
                    )
                )
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 48.dp, vertical = 24.dp)
            ) {
                TopButtons(
                    navigator = navigator,
                    processUuid = processUuid,
                    showDeleteDialogCallback = {
                        openDeletionDialog.value = true
                    },
                )
                DefaultSpacer()
                Content(
                    processTime,
                    intervalTime,
                    info,
                    name,
                    hasAutoChain,
                    gotoName,
                    backgroundUri
                )
                DefaultSpacer()
                var processChainWarning: ProcessDeleteChainWarning? = null
                if (processIsTarget == true) {
                    if (dependantProcesses != null && dependantProcesses!!.dependentProcessIdsAndNames.isNotEmpty()) {
                        processChainWarning = ProcessDeleteChainWarning(
                            title = "Other processes link to this one!",
                            processNames = dependantProcesses!!.dependentProcessIdsAndNames,
                            explanation = "If you delete this process, those others will no longer be able to link to it, meaning they will stop when they try to."
                        )
                    }
                }
                ProcessDeleteRequestedScreen(
                    openDeleteDialog = openDeletionDialog.value,
                    processName = name!!,
                    processChainWarning = processChainWarning,
                    confirmCallback = {
                        openDeletionDialog.value = false
                        processDetailsViewModel.deleteProcess(processUuid)
                        navigator.navigateUp()
                    },
                    dismissCallback = {
                        openDeletionDialog.value = false
                    }
                )
            }
        }
    }
}

@Composable
private fun Content(
    processTime: Int?,
    intervalTime: Int?,
    info: String?,
    name: String?,
    hasAutoChain: Boolean?,
    gotoName: String?,
    backgroundUri: String?
) {
    Box(
        modifier = Modifier.fillMaxHeight(0.9f)
    ) {
        if (null !== processTime && null !== intervalTime) {
            val processTime1 = processTime.toDuration(DurationUnit.SECONDS)
            val intervalTime1 = intervalTime.toDuration(DurationUnit.SECONDS)
            Column(modifier = Modifier.fillMaxSize()) {
                BigTimerText(
                    duration = processTime1,
                    withHours = true,
                    modifier = Modifier
                        .fillMaxWidth(0.66f)
                        .align(Alignment.End)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.66f)
                        .align(Alignment.End)
                ) {
                    AutoSizeText(
                        text = durationToAnnotatedString(
                            duration = intervalTime1,
                            withHours = true,
                            postText = " | ${(processTime1 / intervalTime1).roundToInt()} round(s)"
                        ),
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                if (null != info) {
                    DefaultSpacer()
                    InfoText(infoText = info)
                }
            }
        }
    }
    // spacer
    DefaultSpacer()
    // more process information
    Row {
        val tempName: String = name ?: ""
        Text(text = "Process: '$tempName'")
        if (hasAutoChain == true) {
            Text(text = ", when complete, will lead into '$gotoName'")
        }
    }
    DefaultSpacer()
    Text(text = "Background image URL: $backgroundUri")
}

@Composable
private fun TopButtons(
    navigator: DestinationsNavigator,
    processUuid: String,
    showDeleteDialogCallback: () -> Unit
) {
    Row {
        StandardButton(
            onClick = {
                navigator.navigate(
                    ProcessRunDestination(processUuid = processUuid)
                )
            },
            imageVector = Icons.Default.PlayArrow,
            text = "Start Process"
        )
        DefaultSpacer()
        StandardOutlinedButton(
            onClick = {
                navigator.navigate(ProcessEditDestination(processUuid = processUuid))
            },
            imageVector = Icons.Default.Edit,
            text = "Edit Process"
        )
        Spacer(modifier = Modifier.weight(0.5f))
        StandardOutlinedButton(
            onClick = {
                showDeleteDialogCallback()
            },
            imageVector = Icons.Default.Delete,
            text = "Delete Process"
        )
    }
}
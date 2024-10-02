package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.exner.tools.activitytimerfortv.steps.ProcessDisplayStepAction
import com.exner.tools.activitytimerfortv.ui.tools.BigTimerText
import com.exner.tools.activitytimerfortv.ui.tools.DefaultSpacer
import com.exner.tools.activitytimerfortv.ui.tools.InfoText
import com.exner.tools.activitytimerfortv.ui.tools.MediumTimerAndIntervalText
import com.exner.tools.activitytimerfortv.ui.ProcessRunViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlin.time.Duration.Companion.seconds

@Destination<RootGraph>
@Composable
fun ProcessRun(
    processUuid: String,
    processRunViewModel: ProcessRunViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {

    val displayAction by processRunViewModel.displayAction.observeAsState()
    val numberOfSteps by processRunViewModel.numberOfSteps.observeAsState()
    val currentStepNumber by processRunViewModel.currentStepNumber.observeAsState()
    val hasLoop by processRunViewModel.hasLoop.observeAsState()
    val hasHours by processRunViewModel.hasHours.observeAsState()
    val showStages by processRunViewModel.showStages.observeAsState()
    val backgroundUri by processRunViewModel.backgroundUri.observeAsState()

    processRunViewModel.initialiseRun(
        processUuid = processUuid,
    )

    processRunViewModel.setDoneEventHandler {
        navigator.navigateUp()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = backgroundUri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier.background(
                Brush.linearGradient(
                    listOf(MaterialTheme.colorScheme.background, Color.Transparent)
                )
            ).fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 48.dp, vertical = 24.dp)
                    .fillMaxWidth()
            ) {
                TopButtons(processRunViewModel, navigator)
                DefaultSpacer()
                // show the display, depending on where we are right now
                when (displayAction) {
                    is ProcessDisplayStepAction -> {
                        // TODO
                        val pdAction = (displayAction as ProcessDisplayStepAction)
                        BigTimerText(
                            duration = pdAction.currentIntervalTime.seconds,
                            withHours = hasHours == true,
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .align(Alignment.End)
                        )
                        if (showStages == true) {
                            MediumTimerAndIntervalText(
                                duration = pdAction.currentProcessTime.seconds,
                                withHours = hasHours == true,
                                intervalText = "${pdAction.currentRound} of ${pdAction.totalRounds}",
                                modifier = Modifier
                                    .fillMaxWidth(0.5f)
                                    .align(Alignment.End)
                            )
                        }
                        DefaultSpacer()
                        InfoText(infoText = pdAction.processInfo)
                    }

                    else -> {
                        // nothing to do for us
                    }
                }
            }
        }
    }
}

@Composable
private fun TopButtons(
    processRunViewModel: ProcessRunViewModel,
    navigator: DestinationsNavigator
) {
    Row {
        Button(
            onClick = {
                processRunViewModel.cancel()
                navigator.navigateUp()
            },
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Cancel",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "Cancel")
        }
    }
}

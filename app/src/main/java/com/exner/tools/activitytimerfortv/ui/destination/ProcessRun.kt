package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.exner.tools.activitytimerfortv.steps.ProcessDisplayStepAction
import com.exner.tools.activitytimerfortv.ui.BigTimerText
import com.exner.tools.activitytimerfortv.ui.InfoText
import com.exner.tools.activitytimerfortv.ui.MediumTimerAndIntervalText
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

    processRunViewModel.initialiseRun(
        processUuid = processUuid,
    )

    processRunViewModel.setDoneEventHandler {
        navigator.navigateUp()
    }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(8.dp))
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
                Text(text = "Start Process")
            }

        }
        Spacer(modifier = Modifier.height(8.dp))
        // first, a nice process indicator (if possible)
        if (hasLoop != true) {
            val currentProgress =
                if (numberOfSteps != null && numberOfSteps != 0) currentStepNumber!!.toFloat() / numberOfSteps!! else 0.0f
            LinearProgressIndicator(
                progress = { currentProgress },
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
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
                Spacer(modifier = Modifier.height(16.dp))
                InfoText(infoText = pdAction.processInfo)
            }

            else -> {
                // nothing to do for us
            }
        }
    }
}

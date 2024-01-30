package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.tv.material3.WideButton
import com.exner.tools.activitytimerfortv.steps.ProcessDisplayStepAction
import com.exner.tools.activitytimerfortv.steps.ProcessLeadInDisplayStepAction
import com.exner.tools.activitytimerfortv.ui.BigTimerText
import com.exner.tools.activitytimerfortv.ui.MediumTimerAndIntervalText
import com.exner.tools.activitytimerfortv.ui.ProcessRunViewModel
import com.exner.tools.activitytimerfortv.ui.destination.destinations.SettingsDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalTvMaterial3Api::class)
@Destination
@Composable
fun ProcessRun(
    processId: Long,
    processRunViewModel: ProcessRunViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {

    val displayAction by processRunViewModel.displayAction.observeAsState()
    val numberOfSteps by processRunViewModel.numberOfSteps.observeAsState()
    val currentStepNumber by processRunViewModel.currentStepNumber.observeAsState()
    val hasLoop by processRunViewModel.hasLoop.observeAsState()
    val hasHours by processRunViewModel.hasHours.observeAsState()

    processRunViewModel.initialiseRun(
        processId = processId,
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
            WideButton(
                onClick = {
                    processRunViewModel.cancel()
                    navigator.navigateUp()
                },
                title = { Text(text = "Cancel") },
                icon = { Icon(imageVector = Icons.Filled.Close, contentDescription = "Cancel") }
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    navigator.navigate(SettingsDestination)
                },
            ) {
                Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings")
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
            is ProcessLeadInDisplayStepAction -> {
                // TODO
                val plAction = (displayAction as ProcessLeadInDisplayStepAction)
                BigTimerText(
                    duration = plAction.currentLeadInTime.seconds,
                    withHours = hasHours == true,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .align(Alignment.End)
                )
            }

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
                MediumTimerAndIntervalText(
                    duration = pdAction.currentProcessTime.seconds,
                    withHours = hasHours == true,
                    intervalText = "${pdAction.currentRound} of ${pdAction.totalRounds}",
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .align(Alignment.End)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = pdAction.processInfo, style = MaterialTheme.typography.bodyLarge)
            }

            else -> {
                // nothing to do for us
            }
        }
    }
}
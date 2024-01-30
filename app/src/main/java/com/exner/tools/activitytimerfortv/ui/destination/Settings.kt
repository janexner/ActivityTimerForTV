package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.exner.tools.activitytimerfortv.ui.SettingsViewModel
import com.exner.tools.activitytimerfortv.ui.TextAndSwitch
import com.exner.tools.activitytimerfortv.ui.TextFieldForTimes
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalTvMaterial3Api::class)
@Destination
@Composable
fun Settings(
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {

    val beforeCountingWait by settingsViewModel.beforeCountingWait.collectAsStateWithLifecycle()
    val howLongToWaitBeforeCounting by settingsViewModel.howLongToWaitBeforeCounting.collectAsStateWithLifecycle()
    val countBackwards by settingsViewModel.countBackwards.collectAsStateWithLifecycle()
    val noSounds by settingsViewModel.noSounds.collectAsStateWithLifecycle()

    // show vertically
    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.dp)
        )
        HorizontalDivider()
        TextAndSwitch(text = "Before counting, wait", checked = beforeCountingWait) {
            settingsViewModel.updateBeforeCountingWait(it)
        }
        AnimatedVisibility(visible = beforeCountingWait) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextFieldForTimes(
                    value = howLongToWaitBeforeCounting,
                    label = { Text(text = "How long to wait before counting (seconds)") },
                    onValueChange = {
                        settingsViewModel.updateHowLongToWaitBeforeCounting(it)
                    }
                ) { Text(text = "5") }
            }
        }
        TextAndSwitch(
            text = "Count backwards (down to 0)",
            checked = countBackwards
        ) {
            settingsViewModel.updateCountBackwards(it)
        }
        TextAndSwitch(
            text = "No Sound (count silently)",
            checked = noSounds
        ) {
            settingsViewModel.updateNoSounds(it)
        }
    }
}
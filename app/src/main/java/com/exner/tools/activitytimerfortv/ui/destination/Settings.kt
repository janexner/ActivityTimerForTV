package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.tv.material3.Switch
import androidx.tv.material3.Text
import com.exner.tools.activitytimerfortv.ui.SettingsViewModel
import com.exner.tools.activitytimerfortv.ui.TextFieldForTimes
import com.ramcosta.composedestinations.annotation.Destination

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
    val importAndUploadRestOfChainAutomatically by settingsViewModel.importAndUploadRestOfChainAutomatically.collectAsStateWithLifecycle()

    // show vertically
    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(8.dp)
        )
        HorizontalDivider()
        Row(
            modifier = Modifier.padding(8.dp).fillMaxWidth().clickable {
                settingsViewModel.updateBeforeCountingWait(!beforeCountingWait)
            }
        ) {
            Text(
                text = "Before counting, wait",
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = beforeCountingWait,
                onCheckedChange = { settingsViewModel.updateBeforeCountingWait(it) }
            )
        }
        AnimatedVisibility(visible = beforeCountingWait) {
            Column(
                modifier = Modifier.padding(8.dp).fillMaxWidth()
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
        Row(
            modifier = Modifier.padding(8.dp).fillMaxWidth().clickable {
                settingsViewModel.updateCountBackwards(!countBackwards)
            }
        ) {
            Text(
                text = "Count backwards (down to 0)",
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = countBackwards,
                onCheckedChange = {
                    settingsViewModel.updateCountBackwards(it)
                }
            )
        }
        Row(
            modifier = Modifier.padding(8.dp).fillMaxWidth().clickable {
                settingsViewModel.updateNoSounds(!noSounds)
            }
        ) {
            Text(
                text = "No Sound (count silently)",
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = noSounds,
                onCheckedChange = {
                    settingsViewModel.updateNoSounds(it)
                }
            )
        }
        Row(
            modifier = Modifier.padding(8.dp).fillMaxWidth().clickable {
                settingsViewModel.updateImportAndUploadRestOfChainAutomatically(!noSounds)
            }
        ) {
            Text(
                text = "When selecting a process for import or upload, automatically select rest of chain, too",
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = importAndUploadRestOfChainAutomatically,
                onCheckedChange = {
                    settingsViewModel.updateImportAndUploadRestOfChainAutomatically(it)
                }
            )
        }
    }
}
package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Switch
import androidx.tv.material3.Text
import com.exner.tools.activitytimerfortv.ui.HeaderText
import com.exner.tools.activitytimerfortv.ui.SettingsViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<RootGraph>
@Composable
fun Settings(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {

    val countBackwards by settingsViewModel.countBackwards.collectAsStateWithLifecycle()
    val noSounds by settingsViewModel.noSounds.collectAsStateWithLifecycle()
    val importAndUploadRestOfChainAutomatically by settingsViewModel.importAndUploadRestOfChainAutomatically.collectAsStateWithLifecycle()

    // show vertically
    Column(
        modifier = Modifier
            .padding(horizontal = 48.dp, vertical = 24.dp)
            .fillMaxSize()
    ) {
        HeaderText(text = "Settings")
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .clickable {
                    settingsViewModel.updateCountBackwards(!countBackwards)
                }
        ) {
            Text(
                text = "Count backwards (down to 0)",
                style = MaterialTheme.typography.bodyMedium
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
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .clickable {
                    settingsViewModel.updateNoSounds(!noSounds)
                }
        ) {
            Text(
                text = "No Sound (count silently)",
                style = MaterialTheme.typography.bodyMedium
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
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .clickable {
                    settingsViewModel.updateImportAndUploadRestOfChainAutomatically(!importAndUploadRestOfChainAutomatically)
                }
        ) {
            Text(
                text = "When selecting a process for import or upload, automatically select rest of chain, too",
                style = MaterialTheme.typography.bodyMedium
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


package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.Text
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ImportFromNearbyDeviceDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<RootGraph>
@Composable
fun ManageProcesses(
    navigator: DestinationsNavigator
) {
    Column(
        modifier = Modifier.padding(PaddingValues(horizontal = 48.dp, vertical = 24.dp)),
    ) {
        Text(text = "You can either use the Activity Timer Companion app on your phone to manage processes on your TV, or you can import processes from the Meditation Timer app, also on your phone.")
        Spacer(modifier = Modifier.weight(0.8f))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        ) {
            item {
                Button(onClick = { navigator.navigate(ImportFromNearbyDeviceDestination) }) {
                    Text(text = "Manage processes with Companion app")
                }
            }
            item {
                Button(onClick = { navigator.navigate(ImportFromNearbyDeviceDestination) }) {
                    Text(text = "Import processes from Meditation Timer")
                }
            }
        }
    }
}
package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.Text
import com.exner.tools.activitytimerfortv.R
import com.exner.tools.activitytimerfortv.ui.tools.DefaultSpacer
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ConnectedDestination
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
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        ) {
            item {
                Button(onClick = { navigator.navigate(ConnectedDestination) }) {
                    Text(text = "Manage processes with Companion app")
                }
            }
            item {
                Button(onClick = { navigator.navigate(ImportFromNearbyDeviceDestination) }) {
                    Text(text = "Import processes from Meditation Timer")
                }
            }
        }
        Spacer(modifier = Modifier.weight(0.5f))
        Text(text = "You can either use the Activity Timer Companion app on your phone to manage processes on your TV, or you can import processes from the Meditation Timer app, also on your phone.")
        Spacer(modifier = Modifier.weight(0.5f))
        Text(text = "Get those apps from my web site:")
        DefaultSpacer()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column {
                Text(text = "Activity Timer Companion")
                Image(
                    painter = painterResource(R.drawable.qr_activitytimer_241226),
                    contentDescription = "https://jan-exner.de/software/android/activitytimer"
                )
            }
            Spacer(modifier = Modifier.padding(16.dp))
            Column {
                Text(text = "Meditation Timer")
                Image(
                    painter = painterResource(R.drawable.qr_meditationtimer_241226),
                    contentDescription = "https://jan-exner.de/software/android/meditationtimer"
                )
            }
        }
    }
}
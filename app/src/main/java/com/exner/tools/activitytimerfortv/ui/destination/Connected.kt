package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.exner.tools.activitytimerfortv.ui.ConnectedViewModel
import com.exner.tools.activitytimerfortv.ui.ProcessStateConstants
import com.exner.tools.activitytimerfortv.ui.tools.IconSpacer
import com.exner.tools.activitytimerfortv.ui.tools.StandardButton
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<RootGraph>
@Composable
fun Connected(
    connectedViewModel: ConnectedViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val messages by connectedViewModel.messages.observeAsState(listOf())
    val listState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp, vertical = 24.dp)
    ) {
        // buttons
        Row {
            Spacer(modifier = Modifier.weight(0.9f))
            StandardButton(
                onClick = {
//                    if (processState.currentState == ProcessStateConstants.CANCELLED) {
                        navigator.navigateUp()
//                    } else {
//                        importFromNearbyDeviceViewModel.transitionToNewState(
//                            ProcessStateConstants.CANCELLED,
//                            "Cancelled by user"
//                        )
//                    }
                },
                imageVector = Icons.Default.Clear,
                text = "Cancel"
            )
        }
        // spacer
        Spacer(modifier = Modifier.weight(0.1f))
        LazyColumn(
            modifier = Modifier.padding(PaddingValues(horizontal = 48.dp, vertical = 24.dp)),
            state = listState
        ) {
            items(items = messages) { message ->
                Text(text = message)
            }
        }
    }
}
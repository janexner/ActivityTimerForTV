package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.exner.tools.activitytimerfortv.ui.ConnectedViewModel
import com.exner.tools.activitytimerfortv.ui.destination.wrappers.AskForPermissionsOnTVWrapper
import com.exner.tools.activitytimerfortv.ui.destination.wrappers.EstablishConnectionWrapper
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<RootGraph>(
    wrappers = [AskForPermissionsOnTVWrapper::class, EstablishConnectionWrapper::class]
)
@Composable
fun Connected(
    connectedViewModel: ConnectedViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current

}
package com.exner.tools.activitytimerfortv.ui

import com.exner.tools.activitytimerfortv.ui.destination.wrappers.ConnectedProcessStateConstants

data class ConnectionUIState(
    val currentState: ConnectedProcessStateConstants = ConnectedProcessStateConstants.IDLE
)

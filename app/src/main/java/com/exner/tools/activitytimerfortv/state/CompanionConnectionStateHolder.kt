package com.exner.tools.activitytimerfortv.state

import kotlinx.coroutines.flow.StateFlow

interface CompanionConnectionStateHolder {
    val companionConnectionState: StateFlow<CompanionConnectionState>
    fun updateCompanionConnectionState(isConnectedToCompanion: Boolean)
}
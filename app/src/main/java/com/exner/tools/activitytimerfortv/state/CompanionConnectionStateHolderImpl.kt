package com.exner.tools.activitytimerfortv.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class CompanionConnectionStateHolderImpl @Inject constructor() : CompanionConnectionStateHolder {
    private val _companionConnectionState = MutableStateFlow(CompanionConnectionState())
    override val companionConnectionState: StateFlow<CompanionConnectionState> = _companionConnectionState

    override fun updateCompanionConnectionState(isConnectedToCompanion: Boolean) {
        //atomic modification
        _companionConnectionState.update { current ->
            current.copy(isConnectedToCompanion = isConnectedToCompanion)
        }
    }
}
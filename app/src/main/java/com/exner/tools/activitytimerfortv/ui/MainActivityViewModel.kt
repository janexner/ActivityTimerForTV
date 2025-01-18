package com.exner.tools.activitytimerfortv.ui

import androidx.lifecycle.ViewModel
import com.exner.tools.activitytimerfortv.data.preferences.UserPreferencesManager
import com.exner.tools.activitytimerfortv.state.CompanionConnectionStateHolder
import com.exner.tools.activitytimerfortv.ui.destination.wrappers.ConnectedProcessStateConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userPreferencesManager: UserPreferencesManager,
    val companionConnectionStateHolder: CompanionConnectionStateHolder
) : ViewModel() {

    fun updateConnectedToCompanion(isConnected: Boolean) {
        companionConnectionStateHolder.updateCompanionConnectionState(isConnectedToCompanion = isConnected)
    }

    private val _connectionUIState = MutableStateFlow(ConnectionUIState())
    val connectionUIState: StateFlow<ConnectionUIState> = _connectionUIState
    fun updateConnectionUIState(newState: ConnectedProcessStateConstants) {
        _connectionUIState.value = ConnectionUIState(newState)
    }

}
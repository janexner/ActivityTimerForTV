package com.exner.tools.activitytimerfortv.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.activitytimerfortv.data.preferences.UserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    val beforeCountingWait: StateFlow<Boolean> = userPreferencesManager.beforeCountingWait().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        false
    )
    val howLongToWaitBeforeCounting: StateFlow<Int> = userPreferencesManager.howLongToWaitBeforeCounting().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        5
    )
    val countBackwards: StateFlow<Boolean> = userPreferencesManager.countBackwards().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        false
    )
    val noSounds: StateFlow<Boolean> = userPreferencesManager.noSounds().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        false
    )
    val importAndUploadRestOfChainAutomatically: StateFlow<Boolean> = userPreferencesManager.importAndUploadRestOfChainAutomatically().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        false
    )

    fun updateBeforeCountingWait(newBeforeCountingWait: Boolean) {
        viewModelScope.launch {
            userPreferencesManager.setBeforeCountingWait(newBeforeCountingWait)
        }
    }

    fun updateHowLongToWaitBeforeCounting(newHowLongToWaitBeforeCounting: Int) {
        viewModelScope.launch {
            userPreferencesManager.setHowLongToWaitBeforeCounting(newHowLongToWaitBeforeCounting)
        }
    }

    fun updateCountBackwards(newCountBackwards: Boolean) {
        viewModelScope.launch {
            userPreferencesManager.setCountBackwards(newCountBackwards)
        }
    }

    fun updateNoSounds(newNoSounds: Boolean) {
        viewModelScope.launch {
            userPreferencesManager.setNoSounds(newNoSounds)
        }
    }

    fun updateImportAndUploadRestOfChainAutomatically(doThemAll: Boolean) {
        viewModelScope.launch {
            userPreferencesManager.setImportAndUploadRestOfChainAutomatically(doThemAll)
        }
    }
}
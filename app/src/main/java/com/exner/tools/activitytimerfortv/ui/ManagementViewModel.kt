package com.exner.tools.activitytimerfortv.ui

import androidx.lifecycle.ViewModel
import com.exner.tools.activitytimerfortv.data.persistence.TimerDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManagementViewModel @Inject constructor(
    repository: TimerDataRepository
) : ViewModel() {

    val processes = repository.observeProcesses
    val categories = repository.observeCategories

}
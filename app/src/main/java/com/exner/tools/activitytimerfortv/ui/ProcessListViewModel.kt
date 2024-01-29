package com.exner.tools.activitytimerfortv.ui

import androidx.lifecycle.ViewModel
import com.exner.tools.activitytimerfortv.data.persistence.TimerDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProcessListViewModel @Inject constructor(
    private val repository: TimerDataRepository
): ViewModel() {

    private val observeProcessesRaw = repository.observeProcesses
}
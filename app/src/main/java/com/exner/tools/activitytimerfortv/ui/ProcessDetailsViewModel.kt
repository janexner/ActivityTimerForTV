package com.exner.tools.activitytimerfortv.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.activitytimerfortv.data.persistence.TimerDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProcessDetailsViewModel @Inject constructor(
    private val repository: TimerDataRepository
): ViewModel() {

    private val _uid: MutableLiveData<Long> = MutableLiveData(-1L)
    val uid: LiveData<Long> = _uid

    private val _name: MutableLiveData<String> = MutableLiveData("Name")
    val name: LiveData<String> = _name

    private val _info: MutableLiveData<String> = MutableLiveData("Name")
    val info: LiveData<String> = _info

    private val _processTime: MutableLiveData<String> = MutableLiveData("30")
    val processTime: LiveData<String> = _processTime

    private val _intervalTime: MutableLiveData<String> = MutableLiveData("10")
    val intervalTime: LiveData<String> = _intervalTime

    private val _hasAutoChain: MutableLiveData<Boolean> = MutableLiveData(false)
    val hasAutoChain: LiveData<Boolean> = _hasAutoChain

    private val _gotoId: MutableLiveData<Long> = MutableLiveData(-1L)
    val gotoId: LiveData<Long> = _gotoId

    private val _nextProcessesName: MutableLiveData<String> = MutableLiveData("")
    val nextProcessesName: LiveData<String> = _nextProcessesName

    fun getProcess(processId: Long) {
        if (processId != -1L) {
            _uid.value = processId
            viewModelScope.launch {
                val process = repository.loadProcessById(processId)
                if (process != null) {
                    _name.value = process.name
                    _info.value = process.info
                    _processTime.value = process.processTime.toString()
                    _intervalTime.value = process.intervalTime.toString()
                    _hasAutoChain.value = process.hasAutoChain
                    _gotoId.value = process.gotoId ?: -1L
                    if (process.gotoId != null && process.gotoId != -1L) {
                        val nextProcess = repository.loadProcessById(process.gotoId)
                        if (nextProcess != null) {
                            _nextProcessesName.value = nextProcess.name
                        }
                    }
                }
            }
        }
    }
}
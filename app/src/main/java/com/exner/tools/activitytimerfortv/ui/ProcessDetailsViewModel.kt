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

    private val _uuid: MutableLiveData<String> = MutableLiveData("")
    val uuid: LiveData<String> = _uuid

    private val _processTime: MutableLiveData<String> = MutableLiveData("30")
    val processTime: LiveData<String> = _processTime

    private val _intervalTime: MutableLiveData<String> = MutableLiveData("10")
    val intervalTime: LiveData<String> = _intervalTime

    private val _hasAutoChain: MutableLiveData<Boolean> = MutableLiveData(false)
    val hasAutoChain: LiveData<Boolean> = _hasAutoChain

    private val _gotoUuid: MutableLiveData<String?> = MutableLiveData(null)
    val gotoUuid: LiveData<String?> = _gotoUuid
    private val _gotoName: MutableLiveData<String?> = MutableLiveData(null)
    val gotoName: LiveData<String?> = _gotoName

    private val _nextProcessesName: MutableLiveData<String> = MutableLiveData("")
    val nextProcessesName: LiveData<String> = _nextProcessesName

    fun getProcess(processUuid: String) {
        _uuid.value = processUuid
        viewModelScope.launch {
            val process = repository.loadProcessByUuid(processUuid)
            if (process != null) {
                _name.value = process.name
                _info.value = process.info
                _processTime.value = process.processTime.toString()
                _intervalTime.value = process.intervalTime.toString()
                _hasAutoChain.value = process.hasAutoChain
                _gotoUuid.value = process.gotoUuid
                _gotoName.value = process.gotoName
                if (process.gotoUuid != null && process.gotoUuid != "") {
                    val nextProcess = repository.loadProcessByUuid(process.gotoUuid)
                    if (nextProcess != null) {
                        if (_gotoName.value != nextProcess.name) {
                            // this is weird!
                            _gotoName.value = nextProcess.name
                        }
                    }
                }
            }
        }
    }
}
package com.exner.tools.activitytimerfortv.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.activitytimerfortv.data.persistence.TimerChainingDependencies
import com.exner.tools.activitytimerfortv.data.persistence.TimerDataIdAndName
import com.exner.tools.activitytimerfortv.data.persistence.TimerDataRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel(assistedFactory = ProcessDetailsViewModel.ProcessDetailsViewModelFactory::class)
class ProcessDetailsViewModel @AssistedInject constructor(
    @Assisted val uuid: String,
    private val repository: TimerDataRepository
): ViewModel() {

    private val _uid: MutableLiveData<Long> = MutableLiveData(-1L)
    val uid: LiveData<Long> = _uid

    private val _name: MutableLiveData<String> = MutableLiveData("Name")
    val name: LiveData<String> = _name

    private val _info: MutableLiveData<String> = MutableLiveData("Name")
    val info: LiveData<String> = _info

    private val _processTime: MutableLiveData<Int> = MutableLiveData(30)
    val processTime: LiveData<Int> = _processTime

    private val _intervalTime: MutableLiveData<Int> = MutableLiveData(10)
    val intervalTime: LiveData<Int> = _intervalTime

    private val _hasAutoChain: MutableLiveData<Boolean> = MutableLiveData(false)
    val hasAutoChain: LiveData<Boolean> = _hasAutoChain

    private val _gotoUuid: MutableLiveData<String?> = MutableLiveData(null)
    val gotoUuid: LiveData<String?> = _gotoUuid
    private val _gotoName: MutableLiveData<String?> = MutableLiveData(null)
    val gotoName: LiveData<String?> = _gotoName

    private val _categoryName: MutableLiveData<String?> = MutableLiveData(null)
    val categoryName: LiveData<String?> = _categoryName

    private val _backgroundUri: MutableLiveData<String?> = MutableLiveData(null)
    val backgroundUri: LiveData<String?> = _backgroundUri

    private val _nextProcessesName: MutableLiveData<String> = MutableLiveData("")
    val nextProcessesName: LiveData<String> = _nextProcessesName

    private val _processIsTarget: MutableLiveData<Boolean> = MutableLiveData(false)
    val processIsTarget: LiveData<Boolean> = _processIsTarget

    private val _processChainingDependencies: MutableLiveData<TimerChainingDependencies> = MutableLiveData(null)
    val processChainingDependencies: LiveData<TimerChainingDependencies> = _processChainingDependencies

    init {
        viewModelScope.launch {
            val process = repository.loadProcessByUuid(uuid)
            if (process != null) {
                val category = repository.getCategoryById(process.categoryId)
                var backgroundUri = "https://fototimer.net/assets/activitytimer/bg-default.png"
                if (category != null) {
                    backgroundUri = category.backgroundUri ?: backgroundUri
                }
                backgroundUri = process.backgroundUri ?: backgroundUri

                _name.value = process.name
                _info.value = process.info
                _processTime.value = process.processTime
                _intervalTime.value = process.intervalTime
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
                if (category != null) {
                    _categoryName.value = category.name
                }
                _backgroundUri.value = backgroundUri
                val newDependentProcessUuids = repository.getUuidsOfDependentProcesses(process)
                val newDependentProcesses = mutableListOf<TimerDataIdAndName>()
                newDependentProcessUuids.forEach {
                    val tmpProcess = repository.loadProcessByUuid(it)
                    if (tmpProcess != null) {
                        newDependentProcesses.add(TimerDataIdAndName(it, tmpProcess.name))
                    }
                }
                val chainingDependencies = TimerChainingDependencies(
                    newDependentProcesses
                )
                _processChainingDependencies.value = chainingDependencies
                _processIsTarget.value = true
            }
        }
    }

    fun deleteProcess(processUuid: String) {
        viewModelScope.launch {
            val ftp = repository.loadProcessByUuid(processUuid)
            if (ftp != null) {
                repository.delete(ftp)
            }
        }
    }

    @AssistedFactory
    interface ProcessDetailsViewModelFactory {
        fun create(uuid: String): ProcessDetailsViewModel
    }
}

package com.exner.tools.activitytimerfortv.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.activitytimerfortv.data.persistence.TimerDataRepository
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcess
import com.exner.tools.activitytimerfortv.ui.tools.CategoryListDefinitions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProcessEditViewModel @Inject constructor(
    private val repository: TimerDataRepository
) : ViewModel() {

    private val _uid: MutableLiveData<Long> = MutableLiveData(-1L)
    val uid: LiveData<Long> = _uid

    private val _name: MutableLiveData<String> = MutableLiveData("Name")
    val name: LiveData<String> = _name

    private val _info: MutableLiveData<String> = MutableLiveData("Name")
    val info: LiveData<String> = _info

    private val _uuid: MutableLiveData<String> = MutableLiveData("")
    val uuid: LiveData<String> = _uuid

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

    private val _categoryId: MutableLiveData<Long> =
        MutableLiveData(CategoryListDefinitions.CATEGORY_UID_NONE)
    val categoryId: LiveData<Long> = _categoryId
    private val _categoryName: MutableLiveData<String?> = MutableLiveData(null)
    val categoryName: LiveData<String?> = _categoryName

    private val _backgroundUri: MutableLiveData<String?> = MutableLiveData(null)
    val backgroundUri: LiveData<String?> = _backgroundUri

    private val _nextProcessesName: MutableLiveData<String> = MutableLiveData("")
    val nextProcessesName: LiveData<String> = _nextProcessesName

    fun getProcess(processUuid: String) {
        _uuid.value = processUuid
        viewModelScope.launch {
            val process = repository.loadProcessByUuid(processUuid)
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
            }
        }
    }

    fun updateProcess() {
        if (name.value != null && info.value != null && uuid.value != null && processTime.value != null && intervalTime.value != null && hasAutoChain.value != null && categoryId.value != null && uid.value != null) {
            val updatedProcess = TimerProcess(
                name = name.value!!,
                info = info.value!!,
                uuid = uuid.value!!,
                processTime = processTime.value!!,
                intervalTime = intervalTime.value!!,
                hasAutoChain = hasAutoChain.value!!,
                gotoUuid = gotoUuid.value,
                gotoName = gotoName.value,
                categoryId = categoryId.value!!,
                backgroundUri = backgroundUri.value,
                uid = uid.value!!
            )
            viewModelScope.launch {
                repository.updateProcess(updatedProcess)
            }
        }
    }
}

package com.exner.tools.activitytimerfortv.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.activitytimerfortv.data.persistence.TimerCategoryIdNameCount
import com.exner.tools.activitytimerfortv.data.persistence.TimerDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryEditViewModel @Inject constructor(
    private val repository: TimerDataRepository
): ViewModel() {

    private val _uid: MutableLiveData<Long> = MutableLiveData(-1L)
    val uid: LiveData<Long> = _uid

    private val _name: MutableLiveData<String> = MutableLiveData("Name")
    val name: LiveData<String> = _name

    private val _backgroundUri: MutableLiveData<String?> = MutableLiveData(null)
    val backgroundUri: LiveData<String?> = _backgroundUri

    private val _usage: MutableLiveData<TimerCategoryIdNameCount?> = MutableLiveData(null)
    val usage: LiveData<TimerCategoryIdNameCount?> = _usage

    fun getCategory(categoryUid: Long) {
        _uid.value = categoryUid
        viewModelScope.launch {
            val category = repository.getCategoryById(categoryUid)
            if (category != null) {
                var backgroundUri = "https://fototimer.net/assets/activitytimer/bg-default.png"
                backgroundUri = category.backgroundUri ?: backgroundUri

                _name.value = category.name
                _backgroundUri.value = backgroundUri
            }
            val usage = repository.getCategoryUsageById(categoryUid)
            if (usage != null) {
                _usage.value = usage
            }
        }
    }

    fun setName(newName: String) {
        _name.value = newName
    }

    fun setBackgroundUri(newUri: String) {
        _backgroundUri.value = newUri
    }
}

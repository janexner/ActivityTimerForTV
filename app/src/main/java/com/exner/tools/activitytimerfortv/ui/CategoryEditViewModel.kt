package com.exner.tools.activitytimerfortv.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.activitytimerfortv.data.persistence.TimerCategoryIdNameCount
import com.exner.tools.activitytimerfortv.data.persistence.TimerDataRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = CategoryEditViewModel.CategoryEditViewModelFactory::class)
class CategoryEditViewModel @AssistedInject constructor(
    @Assisted val uid: Long,
    private val repository: TimerDataRepository
): ViewModel() {

    private val _name: MutableLiveData<String> = MutableLiveData("Name")
    val name: LiveData<String> = _name

    private val _backgroundUri: MutableLiveData<String?> = MutableLiveData(null)
    val backgroundUri: LiveData<String?> = _backgroundUri

    private val _usage: MutableLiveData<TimerCategoryIdNameCount?> = MutableLiveData(null)
    val usage: LiveData<TimerCategoryIdNameCount?> = _usage

    init {
        viewModelScope.launch {
            val category = repository.getCategoryById(uid)
            if (category != null) {
                var backgroundUri = "https://fototimer.net/assets/activitytimer/bg-default.png"
                backgroundUri = category.backgroundUri ?: backgroundUri

                _name.value = category.name
                _backgroundUri.value = backgroundUri
            }
            val usage = repository.getCategoryUsageById(uid)
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

    @AssistedFactory
    interface CategoryEditViewModelFactory {
        fun create(uid: Long): CategoryEditViewModel
    }
}

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

@HiltViewModel(assistedFactory = CategoryDetailsViewModel.CategoryDetailsViewModelFactory::class)
class CategoryDetailsViewModel @AssistedInject constructor(
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

    fun deleteCategory(categoryUid: Long) {
        viewModelScope.launch {
            val category = repository.getCategoryById(categoryUid)
            if (category != null) {
                repository.deleteCategory(category)
            }
        }
    }

    @AssistedFactory
    interface CategoryDetailsViewModelFactory {
        fun create(uid: Long): CategoryDetailsViewModel
    }
}

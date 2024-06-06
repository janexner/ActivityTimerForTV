package com.exner.tools.activitytimerfortv.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.activitytimerfortv.data.persistence.TimerDataRepository
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcess
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcessCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProcessListViewModel @Inject constructor(
    private val repository: TimerDataRepository
): ViewModel() {

    val observeProcessesRaw = repository.observeProcesses

    private val _observeProcessesForCurrentCategory =
        MutableStateFlow(emptyList<TimerProcess>())
    val observeProcessesForCurrentCategory: StateFlow<List<TimerProcess>>
        get() = _observeProcessesForCurrentCategory

    val observeCategoriesRaw = repository.observeCategories

    private val _currentCategory = MutableStateFlow(TimerProcessCategory("All", -1L))
    val currentCategory: StateFlow<TimerProcessCategory>
        get() = _currentCategory

    init {
        viewModelScope.launch {
            reReadProcessList()
        }
    }

    private suspend fun reReadProcessList() {
        observeProcessesRaw.collect { itemsList ->
            val filteredItemsList: List<TimerProcess> =
                itemsList.filter { item ->
                    item.categoryId == currentCategory.value.uid || currentCategory.value.uid <= -1L
                }
            _observeProcessesForCurrentCategory.value = filteredItemsList
        }
    }

    fun updateCategoryId(id: Long) {
        if (id == -2L) {
            _currentCategory.value = TimerProcessCategory("All", -2L)
        } else {
            viewModelScope.launch {
                _currentCategory.value =
                    repository.getCategoryById(id) ?: TimerProcessCategory("None", -1L)
            }
        }
        viewModelScope.launch {
            reReadProcessList()
        }
    }

}
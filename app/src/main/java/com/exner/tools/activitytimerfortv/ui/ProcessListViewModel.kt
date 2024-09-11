package com.exner.tools.activitytimerfortv.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.activitytimerfortv.data.persistence.TimerDataRepository
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcess
import com.exner.tools.activitytimerfortv.ui.tools.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProcessListViewModel @Inject constructor(
    repository: TimerDataRepository
): ViewModel() {

    val featuredProcessList: StateFlow<List<TimerProcess>> = flow {
        emit(repository.getFeaturedProcessList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val categoryList: StateFlow<List<Category>> = flow {
        var list = listOf<Category>()
        repository.observeCategories.collect {
            val tempList = it
            tempList.forEach { item ->
                val category = Category(
                    name = item.name,
                    processList = repository.getMovieListByCategory(item.name)
                )
                list = list + listOf(category)
            }
            emit(list)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())
}
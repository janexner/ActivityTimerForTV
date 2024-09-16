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
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class ProcessListViewModel @Inject constructor(
    val repository: TimerDataRepository
) : ViewModel() {

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
                    backgroundUri = item.backgroundUri,
                    processList = repository.getProcessListByCategory(item.name)
                )
                list = list + listOf(category)
            }
            emit(list)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    fun getBackgroundUriForProcessOrCategory(process: TimerProcess): String {
        var result = "https://fototimer.net/assets/activitytimer/bg-default.png"

        // overwrite if a category background if there is one
        runBlocking {
            val category = repository.getCategoryById(process.categoryId)
            if (category != null) {
                result = category.backgroundUri ?: result
            }
        }

        // overwrite with a process background if there is one
        result = process.backgroundUri ?: result

        return result
    }
}
package com.exner.tools.activitytimerfortv.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.activitytimerfortv.data.persistence.TimerDataRepository
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcessCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    private val repository: TimerDataRepository
): ViewModel() {

    val observeCategoriesRaw = repository.observeCategories

    val observeCategoryUsage = repository.observeCategoryUsageCount

    fun createNewCategory(newCategoryName: String) {
        viewModelScope.launch {
            val newCategory = TimerProcessCategory(newCategoryName, null,0)
            repository.insertCategory(newCategory)
        }
    }

    fun updateCategoryName(uid: Long, newName: String) {
        viewModelScope.launch {
            val category = repository.getCategoryById(uid)
            if (null != category) {
                category.name = newName
                repository.updateCategory(category)
            }
        }
    }

    fun deleteAllCategoriesFromListOfIds(listOfIdsToDelete: List<Long>) {
        viewModelScope.launch {
            repository.deleteCategoriesByIdsFromList(listOfIdsToDelete)
        }
    }
}
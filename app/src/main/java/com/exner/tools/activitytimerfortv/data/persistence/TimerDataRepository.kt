package com.exner.tools.activitytimerfortv.data.persistence

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TimerDataRepository @Inject constructor(private val timerDataDAO: TimerDataDAO) {

    val observeProcesses: Flow<List<TimerProcess>> =
        timerDataDAO.observeProcessesAlphabeticallyOrdered()

    val observeCategories: Flow<List<TimerProcessCategory>> =
        timerDataDAO.observeCategoriesAlphabeticallyOrdered()

    val observeCategoryUsageCount: Flow<List<TimerCategoryIdNameCount>> =
        timerDataDAO.observeCategoryUsageCount()


    @WorkerThread
    suspend fun loadProcessByUuid(uuid: String): TimerProcess? {
        return timerDataDAO.getTimerProcessByUuid(uuid)
    }

    @WorkerThread
    suspend fun doesProcessWithUuidExist(uuid: String): Boolean {
        return (timerDataDAO.getTimerProcessByUuid(uuid) !== null)
    }

    @WorkerThread
    suspend fun getUuidsOfDependentProcesses(fotoTimerProcess: TimerProcess): List<String> {
        return timerDataDAO.getUuidsOfDependantProcesses(fotoTimerProcess.uuid)
    }

    @WorkerThread
    suspend fun getFeaturedProcessList(): List<TimerProcess> {
        return timerDataDAO.getAllProcesses().shuffled()
    }

    @WorkerThread
    suspend fun getCategoryById(id: Long): TimerProcessCategory? {
        return timerDataDAO.getCategoryById(id)
    }

    @WorkerThread
    suspend fun getProcessListByCategory(categoryName: String): List<TimerProcess> {
        return timerDataDAO.getProcessesByCategory(categoryName)
    }

    @WorkerThread
    suspend fun getCategoryUsageById(id: Long): TimerCategoryIdNameCount? {
        return timerDataDAO.getCategoryUsageCountForId(id)
    }

    @WorkerThread
    suspend fun insert(timerProcess: TimerProcess) {
        timerDataDAO.insert(timerProcess)
    }

    @WorkerThread
    suspend fun delete(timerProcess: TimerProcess) {
        timerDataDAO.delete(timerProcess)
    }

    @WorkerThread
    suspend fun updateProcess(timerProcess: TimerProcess) {
        timerDataDAO.updateProcess(timerProcess)
    }

    @WorkerThread
    suspend fun insertCategory(category: TimerProcessCategory) {
        timerDataDAO.insertCategory(category)
    }

    @WorkerThread
    suspend fun updateCategory(category: TimerProcessCategory) {
        timerDataDAO.updateCategory(category)
    }

    @WorkerThread
    suspend fun deleteCategoriesByIdsFromList(listOfIds: List<Long>) {
        if (listOfIds.isNotEmpty()) {
            timerDataDAO.deleteCategoriesByIdsFromList(listOfIds)
        }
    }
}
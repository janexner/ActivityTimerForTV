package com.exner.tools.activitytimerfortv.data.persistence

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TimerDataRepository @Inject constructor(private val timerDataDAO: TimerDataDAO) {

    val observeProcesses: Flow<List<TimerProcess>> =
        timerDataDAO.observeProcessesAlphabeticallyOrdered()

    @WorkerThread
    suspend fun loadProcessById(uid: Long) : TimerProcess? {
        return timerDataDAO.getTimerProcess(uid)
    }

    @WorkerThread
    suspend fun doesProcessWithIdExist(id: Long): Boolean {
        return (timerDataDAO.getTimerProcess(id) !== null)
    }

    @WorkerThread
    suspend fun insert(timerProcess: TimerProcess) {
        timerDataDAO.insert(timerProcess)
    }
}
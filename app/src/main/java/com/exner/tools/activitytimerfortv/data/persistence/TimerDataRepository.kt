package com.exner.tools.activitytimerfortv.data.persistence

import androidx.annotation.WorkerThread
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TimerDataRepository @Inject constructor(private val timerDataDAO: TimerDataDAO) {

    val observeProcesses: Flow<List<TimerProcess>> =
        timerDataDAO.observeProcessesAlphabeticallyOrdered()

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
    suspend fun insert(timerProcess: TimerProcess) {
        timerDataDAO.insert(timerProcess)
    }

    @WorkerThread
    suspend fun delete(timerProcess: TimerProcess) {
        timerDataDAO.delete(timerProcess)
    }
}
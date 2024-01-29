package com.exner.tools.activitytimerfortv.data.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerDataDAO {

    @Query("SELECT * FROM timerprocess ORDER BY name ASC")
    fun observeProcessesAlphabeticallyOrdered(): Flow<List<TimerProcess>>

    @Query("SELECT * FROM timerprocess WHERE uid=:id")
    suspend fun getTimerProcess(id : Long): TimerProcess?

    @Insert
    suspend fun insert(process: TimerProcess)
}
package com.exner.tools.activitytimerfortv.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TimerProcess::class],
    version = 1,
    exportSchema = false
)
abstract class TimerDataRoomDatabase : RoomDatabase() {
    abstract fun processDAO(): TimerDataDAO
}
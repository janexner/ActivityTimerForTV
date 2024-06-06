package com.exner.tools.activitytimerfortv.data.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TimerProcessCategory(
    @ColumnInfo(name = "name") var name : String,
    @PrimaryKey(autoGenerate = true) val uid: Long = 0
)
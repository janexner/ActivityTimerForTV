package com.exner.tools.activitytimerfortv.data

import com.exner.tools.activitytimerfortv.data.persistence.TimerProcess
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcessCategory
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AllDataHolder(
    val processes: List<TimerProcess>,
    val categories: List<TimerProcessCategory>
)

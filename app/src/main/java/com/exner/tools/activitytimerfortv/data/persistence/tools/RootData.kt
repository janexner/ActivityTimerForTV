package com.exner.tools.activitytimerfortv.data.persistence.tools

import com.exner.tools.activitytimerfortv.data.persistence.TimerProcess
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcessCategory

data class RootData(
    val processes: List<TimerProcess>,
    val categories: List<TimerProcessCategory>
)

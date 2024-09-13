package com.exner.tools.activitytimerfortv.ui.tools

import com.exner.tools.activitytimerfortv.data.persistence.TimerProcess

data class Category(
    val name: String,
    val backgroundUri: String?,
    val processList: List<TimerProcess>
)


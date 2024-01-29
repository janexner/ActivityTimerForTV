package com.exner.tools.activitytimerfortv

import android.app.Application
import com.google.android.material.color.DynamicColors

class ActivityTimerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // let's try dynamic colours
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
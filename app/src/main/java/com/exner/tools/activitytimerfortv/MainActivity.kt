package com.exner.tools.activitytimerfortv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.tv.material3.Surface
import com.exner.tools.activitytimerfortv.ui.destination.NavGraphs
import com.exner.tools.activitytimerfortv.ui.theme.ActivityTimerForTVTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.rememberNavHostEngine
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val engine = rememberNavHostEngine()
            val navController = engine.rememberNavController()

            ActivityTimerForTVTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape
                ) {
                    DestinationsNavHost(
                        navController = navController,
                        navGraph = NavGraphs.root
                    ) {
                    }
                }
            }
        }
    }
}

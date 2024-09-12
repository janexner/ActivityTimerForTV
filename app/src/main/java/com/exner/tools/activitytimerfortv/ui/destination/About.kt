package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.exner.tools.activitytimerfortv.BuildConfig
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph

@Destination<RootGraph>
@Composable
fun About() {
    Column(
        modifier = Modifier.padding(horizontal = 58.dp, vertical = 36.dp)
    ) {
        Text(
            text = "About Activity Timer for TV",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Activity Timer for TV ${BuildConfig.VERSION_NAME}",
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.width(32.dp))
            AboutText()
        }
    }
}

@Composable
fun AboutText() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Activity Timer for TV is a flexible timer application that can be used for timed tasks, simple or complex.",
        )
        Text(
            text = "In simple terms, Activity Timer for TV counts and beeps.",
            modifier = Modifier.padding(0.dp, 16.dp)
        )
        Text(
            text = "Use cases:",
        )
        Text(
            text = "Meditation",
            modifier = Modifier.padding(32.dp, 4.dp)
        )
        Text(
            text = "Exercise",
            modifier = Modifier.padding(32.dp, 4.dp)
        )
        Text(
            text = "Any repetitive task that you do",
            modifier = Modifier.padding(32.dp, 4.dp)
        )
        Text(
            text = "Activity Timer for TV is derived from Foto Timer which started as an app for Palm OS in the 90s.",
            modifier = Modifier.padding(0.dp, 16.dp)
        )
        Text(
            text = "It runs on Android TVs running Android 10 or later. I aim to support the latest 3 versions of Android.",
        )
    }
}
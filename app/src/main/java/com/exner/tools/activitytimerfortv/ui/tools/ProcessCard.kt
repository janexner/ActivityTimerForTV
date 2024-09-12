package com.exner.tools.activitytimerfortv.ui.tools

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.exner.tools.activitytimerfortv.R
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcess

@Composable
fun ProcessCard(
    modifier: Modifier = Modifier,
    process: TimerProcess,
    onClick: (TimerProcess) -> Unit
) {
    Card(
        onClick = { onClick(process) },
        modifier = modifier
            .widthIn(max = 320.dp)
            .aspectRatio(16f / 9f),
    ) {
        val backgroundColour = MaterialTheme.colorScheme.background

        Box {
            AsyncImage(
                model = "https://fototimer.net/assets/activitytimer/bg-breathing1.png",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                contentAlignment = Alignment.BottomStart,
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        val brush = Brush.horizontalGradient(
                            listOf(backgroundColour, Color.Transparent)
                        )
                        drawRect(brush)
                    }
            ) {
                Column() {
                    Text(text = process.name)
                }
            }
        }
    }
}
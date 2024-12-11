package com.exner.tools.activitytimerfortv.ui.tools

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcess

@Composable
fun ProcessCard(
    modifier: Modifier = Modifier,
    process: TimerProcess,
    backgroundUriFallback: String?,
    onClick: () -> Unit,
    isExisting: Boolean = false
) {
    Card(
        onClick = { onClick() },
        modifier = modifier
            .widthIn(max = 320.dp)
            .aspectRatio(16f / 9f),
    ) {
        val backgroundColour = MaterialTheme.colorScheme.background
        val backgroundUri = process.backgroundUri
            ?: (backgroundUriFallback ?: "https://fototimer.net/assets/activitytimer/bg-breathing.png")


        Box {
            AsyncImage(
                model = backgroundUri,
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
                    .padding(4.dp)
            ) {
                Column() {
                    Text(
                        text = process.name,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (isExisting) {
                        Spacer(modifier = modifier.height(4.dp))
                        Text(
                            text = "This process exists locally. You can update your local version with this imported one, or save the imported process as a copy.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

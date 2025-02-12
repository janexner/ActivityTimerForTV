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
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.exner.tools.activitytimerfortv.data.persistence.TimerCategoryIdNameCount
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcessCategory

@Composable
fun CategoryCard(
    modifier: Modifier = Modifier,
    category: TimerProcessCategory,
    usage: TimerCategoryIdNameCount?,
    backgroundUriFallback: String?,
    onClick: () -> Unit
) {
    Card(
        onClick = { onClick() },
        modifier = modifier
            .widthIn(max = 320.dp)
            .aspectRatio(16f / 9f),
    ) {
        val backgroundColour = MaterialTheme.colorScheme.background
        val backgroundUri = category.backgroundUri
            ?: (backgroundUriFallback ?: "https://fototimer.net/assets/activitytimer/bg-breathing.png")

        var usageText = "Unused"
        if (null != usage) {
            if (usage.usageCount > 0) {
                usageText = "Used in ${usage.usageCount} processes"
            }
        }

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
            ) {
                Column() {
                    Text(text = category.name)
                    DefaultSpacer()
                    Text(text = usageText)
                }
            }
        }
    }
}

@Composable
fun CompactCategoryCard(
    modifier: Modifier = Modifier,
    category: TimerProcessCategory,
    usage: TimerCategoryIdNameCount?,
    backgroundUriFallback: String?,
    onClick: () -> Unit
) {
    Card(
        onClick = { onClick() },
        modifier = modifier
            .widthIn(max = 200.dp)
            .aspectRatio(16f / 9f),
    ) {
        val backgroundColour = MaterialTheme.colorScheme.background
        val backgroundUri = category.backgroundUri
            ?: (backgroundUriFallback ?: "https://fototimer.net/assets/activitytimer/bg-breathing.png")

        var usageText = "Unused"
        if (null != usage) {
            if (usage.usageCount > 0) {
                usageText = "Used in ${usage.usageCount} processes"
            }
        }

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
            ) {
                Column() {
                    Text(text = category.name)
                    DefaultSpacer()
                    Text(text = usageText)
                }
            }
        }
    }
}

package com.exner.tools.activitytimerfortv.ui.tools

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
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
        AsyncImage(
            model = "https://fototimer.net/assets/activitytimer/bg-breathing1.png",
            contentDescription = process.name,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.placeholder)
        )
    }
}
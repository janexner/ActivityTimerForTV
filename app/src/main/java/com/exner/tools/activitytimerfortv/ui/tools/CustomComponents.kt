package com.exner.tools.activitytimerfortv.ui.tools

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import java.util.Locale
import kotlin.time.Duration

@Composable
fun durationToAnnotatedString(
    duration: Duration,
    withHours: Boolean,
    postText: String? = null
): AnnotatedString {
    // convert seconds to "00:00" style string
    val output = duration.toComponents { hours, minutes, seconds, _ ->
        String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    }
    val tmp = output.split(":")
    val styledOutput = buildAnnotatedString {
        var myStyle = SpanStyle()
        if (withHours) {
            if ("00" == tmp[0]) {
                myStyle = SpanStyle(color = LocalContentColor.current.copy(alpha = 0.2f))
            }
            withStyle(style = myStyle) {
                append(tmp[0])
            }
            append(":")
        }
        myStyle = if ("00" == tmp[1]) {
            SpanStyle(color = LocalContentColor.current.copy(alpha = 0.2f))
        } else {
            SpanStyle(color = LocalContentColor.current.copy(alpha = 1f))
        }
        withStyle(style = myStyle) {
            append(tmp[1])
        }
        append(":")
        append(tmp[2])
        if (postText !== null) {
            append(postText)
        }
    }

    return styledOutput
}

@Composable
fun BigTimerText(duration: Duration, withHours: Boolean, modifier: Modifier = Modifier) {
    AutoSizeText(
        text = durationToAnnotatedString(duration, withHours),
        modifier = modifier,
        maxLines = 1,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
fun MediumTimerAndIntervalText(
    duration: Duration,
    withHours: Boolean,
    intervalText: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        AutoSizeText(
            text = durationToAnnotatedString(duration, withHours, " | Round $intervalText"),
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun InfoText(
    infoText: String,
    modifier: Modifier = Modifier
) {
    val annotatedInfoText = AnnotatedString(infoText)
    Row(modifier = modifier) {
        Text(
            text = annotatedInfoText,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun HeaderText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.displaySmall,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
    )
}

@Composable
fun BodyText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier,
    )
}

@Composable
fun DefaultSpacer() {
    Spacer(modifier = Modifier.size(16.dp))
}
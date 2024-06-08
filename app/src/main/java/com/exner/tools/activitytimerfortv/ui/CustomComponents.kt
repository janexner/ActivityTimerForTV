package com.exner.tools.activitytimerfortv.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.exner.tools.activitytimerfortv.ui.tools.AutoSizeText
import java.util.Locale
import kotlin.math.roundToInt
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
    Row(modifier = modifier) {
        AutoSizeText(
            text = infoText,
            alignment = Alignment.BottomStart,
            color = MaterialTheme.colorScheme.onSurface,
            lineSpacingRatio = 1.75f,
        )
    }
}

@Composable
fun TimerDisplay(
    processTime: Duration,
    intervalTime: Duration,
    info: String?,
    forceWithHours: Boolean = false
) {
    Column(modifier = Modifier.fillMaxSize()) {
        BigTimerText(
            duration = processTime,
            withHours = forceWithHours,
            modifier = Modifier
                .fillMaxWidth(0.66f)
                .align(Alignment.End)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(0.66f)
                .align(Alignment.End)
        ) {
            AutoSizeText(
                text = durationToAnnotatedString(
                    duration = intervalTime,
                    withHours = forceWithHours,
                    postText = " | ${(processTime / intervalTime).roundToInt().toString()} round(s)"
                ),
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        if (null != info) {
            Spacer(modifier = Modifier.height(16.dp))
            InfoText(infoText = info)
        }
    }
}

@Composable
fun TextFieldForTimes(
    value: Int,
    label: @Composable (() -> Unit)?,
    onValueChange: (Int) -> Unit,
    placeholder: @Composable (() -> Unit)? = null,
) {
    var text by remember(value) { mutableStateOf(value.toString()) }
    TextField(
        value = text,
        label = label,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        onValueChange = { raw ->
            text = raw
            val parsed = text.toIntOrNull() ?: 0
            onValueChange(parsed)
        },
        placeholder = placeholder,
        textStyle = MaterialTheme.typography.bodyLarge
    )
}

@Composable
fun HeaderText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
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
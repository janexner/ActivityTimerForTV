package com.exner.tools.activitytimerfortv.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.LocalTextStyle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import java.util.Locale
import kotlin.time.Duration

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun durationToAnnotatedString(duration: Duration, withHours: Boolean, postText: String? = null): AnnotatedString {
    // convert seconds to "00:00" style string
    val output = duration.toComponents { hours, minutes, seconds, _ ->
        String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    }
    val tmp = output.split(":")
    val styledOutput = buildAnnotatedString {
        var myStyle = SpanStyle()
        if (withHours) {
            if ("00" == tmp[0]) {
                myStyle = SpanStyle(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            }
            withStyle(style = myStyle) {
                append(tmp[0])
            }
            append(":")
        }
        myStyle = if ("00" == tmp[1]) {
            SpanStyle(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
        } else {
            SpanStyle(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f))
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

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun BigTimerText(duration: Duration, withHours: Boolean, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier) {
        AutoSizeText(
            text = durationToAnnotatedString(duration, withHours),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            fontSize = 499.dp.toTextDp(),
            constraints = constraints
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MediumTimerAndIntervalText(
    duration: Duration,
    withHours: Boolean,
    intervalText: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        val completeText = durationToAnnotatedString(duration, withHours, " | Round $intervalText")
        BoxWithConstraints {
            AutoSizeText(
                text = completeText,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                fontSize = 99.dp.toTextDp(),
                constraints = constraints
            )
        }
    }
}

@Composable
fun Dp.toTextDp(): TextUnit = textSp(density = LocalDensity.current)

private fun Dp.textSp(density: Density): TextUnit = with(density) {
    this@textSp.toSp()
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun AutoSizeText(
    text: AnnotatedString,
    constraints: Constraints,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    val tm = TextMeasurer(
        defaultFontFamilyResolver = LocalFontFamilyResolver.current,
        defaultDensity = LocalDensity.current,
        defaultLayoutDirection = LayoutDirection.Ltr
    )
    var shrunkFontSize = fontSize
    // measure
    var measure = tm.measure(
        text = text,
        style = TextStyle.Default.copy(
            fontSize = shrunkFontSize,
        ),
        maxLines = 1,
        constraints = constraints
    )
    while (measure.hasVisualOverflow) {
        shrunkFontSize = (shrunkFontSize.value - 2).sp
        measure = tm.measure(
            text = text,
            style = TextStyle.Default.copy(
                fontSize = shrunkFontSize,
            ),
            maxLines = 1,
            constraints = constraints
        )
    }
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = shrunkFontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        onTextLayout = onTextLayout,
        style = style
    )
}
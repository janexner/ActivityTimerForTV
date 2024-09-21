package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.exner.tools.activitytimerfortv.ui.BigTimerText
import com.exner.tools.activitytimerfortv.ui.DefaultSpacer
import com.exner.tools.activitytimerfortv.ui.InfoText
import com.exner.tools.activitytimerfortv.ui.ProcessDetailsViewModel
import com.exner.tools.activitytimerfortv.ui.durationToAnnotatedString
import com.exner.tools.activitytimerfortv.ui.tools.AutoSizeText
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ProcessDeleteDestination
import com.ramcosta.composedestinations.generated.destinations.ProcessEditDestination
import com.ramcosta.composedestinations.generated.destinations.ProcessRunDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlin.math.roundToInt
import kotlin.time.DurationUnit
import kotlin.time.toDuration


@Destination<RootGraph>
@Composable
fun ProcessDetails(
    processUuid: String,
    processDetailsViewModel: ProcessDetailsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {

    val name by processDetailsViewModel.name.observeAsState()
    val info by processDetailsViewModel.info.observeAsState()
    val processTime by processDetailsViewModel.processTime.observeAsState()
    val intervalTime by processDetailsViewModel.intervalTime.observeAsState()
    val hasAutoChain by processDetailsViewModel.hasAutoChain.observeAsState()
    val gotoName by processDetailsViewModel.gotoName.observeAsState()
    val backgroundUri by processDetailsViewModel.backgroundUri.observeAsState()

    processDetailsViewModel.getProcess(processUuid)

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = backgroundUri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(MaterialTheme.colorScheme.background, Color.Transparent)
                    )
                )
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 48.dp, vertical = 24.dp)
            ) {
                TopButtons(navigator, processUuid)
                DefaultSpacer()
                Content(
                    processTime,
                    intervalTime,
                    info,
                    name,
                    hasAutoChain,
                    gotoName,
                    backgroundUri
                )
            }
        }
    }
}

@Composable
private fun Content(
    processTime: Int?,
    intervalTime: Int?,
    info: String?,
    name: String?,
    hasAutoChain: Boolean?,
    gotoName: String?,
    backgroundUri: String?
) {
    Box(
        modifier = Modifier.fillMaxHeight(0.9f)
    ) {
        if (null !== processTime && null !== intervalTime) {
            val processTime1 = processTime.toDuration(DurationUnit.SECONDS)
            val intervalTime1 = intervalTime.toDuration(DurationUnit.SECONDS)
            Column(modifier = Modifier.fillMaxSize()) {
                BigTimerText(
                    duration = processTime1,
                    withHours = true,
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
                            duration = intervalTime1,
                            withHours = true,
                            postText = " | ${(processTime1 / intervalTime1).roundToInt()} round(s)"
                        ),
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                if (null != info) {
                    DefaultSpacer()
                    InfoText(infoText = info)
                }
            }
        }
    }
    // spacer
    Spacer(modifier = Modifier.size(16.dp))
    // more process information
    Row {
        val tempName: String = name ?: ""
        Text(text = "Process: '$tempName'")
        if (hasAutoChain == true) {
            Text(text = ", when complete, will lead into '$gotoName'")
        }
    }
    Spacer(modifier = Modifier.size(16.dp))
    Text(text = "Background image URL: $backgroundUri")
}

@Composable
private fun TopButtons(
    navigator: DestinationsNavigator,
    processUuid: String
) {
    Row {
        Button(
            onClick = {
                navigator.navigate(
                    ProcessRunDestination(processUuid = processUuid)
                )
            },
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Start Process",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "Start Process")
        }
        Spacer(modifier = Modifier.size(16.dp))
        OutlinedButton(
            onClick = {
                navigator.navigate(ProcessEditDestination(processUuid = processUuid))
            },
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Edit Process",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "Edit Process")
        }
        Spacer(modifier = Modifier.weight(0.5f))
        OutlinedButton(
            onClick = {
                navigator.navigate(ProcessDeleteDestination(processUuid = processUuid))
            },
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete Process"
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "Delete Process")
        }
    }
}
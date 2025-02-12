package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.exner.tools.activitytimerfortv.ui.ExportDataViewModel
import com.exner.tools.activitytimerfortv.ui.tools.BodyText
import com.exner.tools.activitytimerfortv.ui.tools.DefaultSpacer
import com.exner.tools.activitytimerfortv.ui.tools.StandardButton
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<RootGraph>
@Composable
fun ExportData(
    exportDataViewModel: ExportDataViewModel = hiltViewModel(),
    destinationsNavigator: DestinationsNavigator
) {
    val context = LocalContext.current

    val process by exportDataViewModel.allProcesses.collectAsState(
        emptyList()
    )
    val categories by exportDataViewModel.allCategories.collectAsState(
        emptyList()
    )

    Column(
        modifier = Modifier
            .padding(horizontal = 48.dp, vertical = 24.dp)
            .fillMaxSize()
    ) {
        Row {
            StandardButton(
                onClick = {
                    exportDataViewModel.commitExport(context) {
                        // TODO
                        destinationsNavigator.navigateUp()
                    }
                },
                imageVector = Icons.Default.PlayArrow,
                text = "Export data"
            )
        }
        DefaultSpacer()
        BodyText(text = "These processes can be exported:")
        DefaultSpacer()
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.3f)
        ) {
            items(process) {
                BodyText(text = it.name)
            }
        }
        DefaultSpacer()
        BodyText(text = "These categories can be exported:")
        DefaultSpacer()
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.3f)
        ) {
            items(categories) {
                BodyText(text = it.name)
            }
        }
    }
}
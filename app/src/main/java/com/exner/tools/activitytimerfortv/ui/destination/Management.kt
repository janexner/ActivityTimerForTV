package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.exner.tools.activitytimerfortv.ui.ManagementViewModel
import com.exner.tools.activitytimerfortv.ui.tools.CompactCategoryCard
import com.exner.tools.activitytimerfortv.ui.tools.CompactProcessCard
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ExportDataDestination
import com.ramcosta.composedestinations.generated.destinations.ManageProcessesDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<RootGraph>
@Composable
fun Management(
    managementViewModel: ManagementViewModel = hiltViewModel(),
    destinationsNavigator: DestinationsNavigator
) {

    val processes by managementViewModel.processes.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )
    val categories by managementViewModel.categories.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(horizontal = 48.dp, vertical = 24.dp)
    ) {
        // other stuff
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            ) {
                item {
                    Button(onClick = { destinationsNavigator.navigate(ManageProcessesDestination) }) {
                        Text(text = "Import data from file")
                    }
                }
                item {
                    Button(onClick = { destinationsNavigator.navigate(ExportDataDestination) }) {
                        Text(text = "Export data to file")
                    }
                }
            }
        }
        // processes
        item {
            Text(
                text = "Processes",
                style = MaterialTheme.typography.titleMedium
            )
        }
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            ) {
                items(items = processes, key = { "process-${it.uid}" }) { process ->
                    CompactProcessCard(
                        process = process,
                        modifier = Modifier,
                        backgroundUriFallback = null,
                        onClick = { }
                    )
                }
            }
        }
        // categories
        item {
            Text(
                text = "Categories",
                style = MaterialTheme.typography.titleMedium
            )
        }
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            ) {
                items(items = categories, key = { "category-${it.uid}" }) { category ->
                    CompactCategoryCard(
                        modifier = Modifier,
                        category = category,
                        usage = null,
                        backgroundUriFallback = null,
                        onClick = {}
                    )
                }
            }
        }
    }
}
package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Checkbox
import androidx.tv.material3.ClassicCard
import androidx.tv.material3.Text
import com.exner.tools.activitytimerfortv.data.persistence.TimerCategoryIdNameCount
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcessCategory
import com.exner.tools.activitytimerfortv.ui.BodyText
import com.exner.tools.activitytimerfortv.ui.CategoryListViewModel
import com.exner.tools.activitytimerfortv.ui.HeaderText
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<RootGraph>
@Composable
fun CategoryBulkDelete(
    categoryListViewModel: CategoryListViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val categories: List<TimerProcessCategory> by categoryListViewModel.observeCategoriesRaw.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    val categoryUsage: List<TimerCategoryIdNameCount> by categoryListViewModel.observeCategoryUsage.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    val listOfCategoryIdsToDelete = remember {
        mutableStateListOf<Long>()
    }

    val openAlertDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 250.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(count = categories.size) { meditationTimerCategory ->
                val category = categories[meditationTimerCategory]
                var supText = "Unused"
                val usage = categoryUsage.firstOrNull {
                    it.uid == category.uid
                }
                if (usage != null) {
                    if (usage.usageCount > 0) {
                        supText = "Used in ${usage.usageCount} process(es)"
                    }
                }
                ClassicCard(
                    modifier = Modifier.padding(8.dp),
                    onClick = {
                              // TODO select or deselect
                    },
                    subtitle = {
                        Checkbox(
                            checked = listOfCategoryIdsToDelete.contains(category.uid),
                            onCheckedChange = { checked ->
                                if (checked) {
                                    listOfCategoryIdsToDelete.add(category.uid)
                                } else {
                                    listOfCategoryIdsToDelete.remove(category.uid)
                                }
                            })
                    },
                    title = { HeaderText(text = category.name) },
                    description = { BodyText(text = supText) },
                    image = {}
                )
            }
        }
        // Alert Dialog
        if (openAlertDialog.value) {
            AlertDialog(
                icon = {},
                title = { Text(text = "Delete?") },
                text = { Text(text = "Delete ${listOfCategoryIdsToDelete.size} categories?") },
                onDismissRequest = { openAlertDialog.value = false },
                dismissButton = {
                    TextButton(onClick = {
                        openAlertDialog.value = false
                    }) {
                        Text(text = "No")
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        categoryListViewModel.deleteAllCategoriesFromListOfIds(
                            listOfCategoryIdsToDelete
                        )
                        openAlertDialog.value = false
                        navigator.navigateUp()
                    }) {
                        Text(text = "Yes, delete")
                    }
                }
            )
        }
    }
}

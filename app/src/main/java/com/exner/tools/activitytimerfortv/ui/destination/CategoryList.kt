package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.material3.ClassicCard
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.exner.tools.activitytimerfortv.data.persistence.TimerCategoryIdNameCount
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcessCategory
import com.exner.tools.activitytimerfortv.ui.BodyText
import com.exner.tools.activitytimerfortv.ui.CategoryListViewModel
import com.exner.tools.activitytimerfortv.ui.HeaderText
import com.exner.tools.activitytimerfortv.ui.tools.ActivityTimerNavigationDrawerContent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun CategoryList(
    categoryListViewModel: CategoryListViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val categories: List<TimerProcessCategory> by categoryListViewModel.observeCategoriesRaw.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    val categoryUsage: List<TimerCategoryIdNameCount> by categoryListViewModel.observeCategoryUsage.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    val openDialog = remember { mutableStateOf(false) }

    val openDialogCategory = remember {
        mutableStateOf<TimerProcessCategory?>(null)
    }

    NavigationDrawer(drawerContent = {
        ActivityTimerNavigationDrawerContent(
            navigator = navigator,
            defaultSelectedIndex = 3, // this should not be a constant!
        )
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            TvLazyVerticalGrid(
                columns = TvGridCells.Adaptive(minSize = 250.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(count = categories.size) { index ->
                    val category = categories[index]
                    Surface(
                        modifier = Modifier
                            .clickable {
                                openDialogCategory.value = category
                                openDialog.value = true
                            },
                    ) {
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
                            onClick = {},
                            title = { HeaderText(text = category.name) },
                            subtitle = { BodyText(text = supText) },
                            description = {},
                            image = {}
                        )
                    }
                }
            }
            // dialog for making a new category
            if (openDialog.value) {
                BasicAlertDialog(
                    onDismissRequest = {
                        // Dismiss the dialog when the user clicks outside the dialog or on the back
                        // button. If you want to disable that functionality, simply use an empty
                        // onDismissRequest.
                        openDialog.value = false
                    }
                ) {
                    Surface(
                        modifier = Modifier
                            .wrapContentWidth()
                            .wrapContentHeight(),
                        shape = MaterialTheme.shapes.large,
                        tonalElevation = AlertDialogDefaults.TonalElevation
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            var newCategoryName by remember {
                                mutableStateOf(openDialogCategory.value?.name ?: "New Category")
                            }
                            TextField(
                                value = newCategoryName,
                                onValueChange = {
                                    newCategoryName = it
                                },
                                label = { Text(text = "Category name") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            TextButton(
                                onClick = {
                                    val uid = openDialogCategory.value?.uid ?: -1
                                    if (uid < 0) {
                                        categoryListViewModel.createNewCategory(newCategoryName)
                                    } else {
                                        categoryListViewModel.updateCategoryName(
                                            uid,
                                            newCategoryName
                                        )
                                    }
                                    openDialog.value = false
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Save")
                            }
                        }
                    }
                }
            }
        }
    }
}

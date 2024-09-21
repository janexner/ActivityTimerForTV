package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.exner.tools.activitytimerfortv.data.persistence.TimerCategoryIdNameCount
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcessCategory
import com.exner.tools.activitytimerfortv.ui.CategoryListViewModel
import com.exner.tools.activitytimerfortv.ui.DefaultSpacer
import com.exner.tools.activitytimerfortv.ui.tools.CategoryCard
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.CategoryDetailsDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp, vertical = 24.dp)
    ) {
        Text(text = "Categories", style = MaterialTheme.typography.displaySmall)
        DefaultSpacer()
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 250.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(count = categories.size) { index ->
                val category = categories[index]
                val usage = categoryUsage.firstOrNull {
                    it.uid == category.uid
                }
                CategoryCard(
                    modifier = Modifier,
                    category = category,
                    usage = usage,
                    backgroundUriFallback = "https://fototimer.net/assets/activitytimer/bg-default.png",
                    onClick = { navigator.navigate(CategoryDetailsDestination(category.uid)) }
                )
            }
        }
        // dialog for making a new category
//            if (openDialog.value) {
//                BasicAlertDialog(
//                    onDismissRequest = {
//                        // Dismiss the dialog when the user clicks outside the dialog or on the back
//                        // button. If you want to disable that functionality, simply use an empty
//                        // onDismissRequest.
//                        openDialog.value = false
//                    }
//                ) {
//                    Surface(
//                        modifier = Modifier
//                            .wrapContentWidth()
//                            .wrapContentHeight(),
//                        shape = MaterialTheme.shapes.large,
//                        tonalElevation = AlertDialogDefaults.TonalElevation
//                    ) {
//                        Column(modifier = Modifier.padding(16.dp)) {
//                            var newCategoryName by remember {
//                                mutableStateOf(openDialogCategory.value?.name ?: "New Category")
//                            }
//                            TextField(
//                                value = newCategoryName,
//                                onValueChange = {
//                                    newCategoryName = it
//                                },
//                                label = { Text(text = "Category name") },
//                                modifier = Modifier.fillMaxWidth(),
//                                singleLine = true,
//                                textStyle = MaterialTheme.typography.bodyLarge
//                            )
//                            Spacer(modifier = Modifier.height(24.dp))
//                            TextButton(
//                                onClick = {
//                                    val uid = openDialogCategory.value?.uid ?: -1
//                                    if (uid < 0) {
//                                        categoryListViewModel.createNewCategory(newCategoryName)
//                                    } else {
//                                        categoryListViewModel.updateCategoryName(
//                                            uid,
//                                            newCategoryName
//                                        )
//                                    }
//                                    openDialog.value = false
//                                },
//                                modifier = Modifier.align(Alignment.End)
//                            ) {
//                                Text("Save")
//                            }
//                        }
//                    }
//                }
//            }
    }
}

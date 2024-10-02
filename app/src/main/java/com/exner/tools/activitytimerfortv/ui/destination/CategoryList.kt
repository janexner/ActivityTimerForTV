package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.exner.tools.activitytimerfortv.data.persistence.TimerCategoryIdNameCount
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcessCategory
import com.exner.tools.activitytimerfortv.ui.CategoryListViewModel
import com.exner.tools.activitytimerfortv.ui.tools.CategoryCard
import com.exner.tools.activitytimerfortv.ui.tools.CategoryCreationScreen
import com.exner.tools.activitytimerfortv.ui.tools.DefaultSpacer
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

    val openCreateDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp, vertical = 24.dp)
    ) {
        Row {
            Button(
                enabled = true,
                onClick = {
                    openCreateDialog.value = true
                },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add a Category",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = "Add a Category")
            }
        }
        DefaultSpacer()
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
        CategoryCreationScreen(
            openCreateDialog = openCreateDialog.value,
            confirmCallback = { name ->
                openCreateDialog.value = false
                categoryListViewModel.createNewCategory(name)
            },
            dismissCallback = {
                openCreateDialog.value = false
            }
        )
    }
}

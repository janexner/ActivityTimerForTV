package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button
import androidx.tv.material3.Carousel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.exner.tools.activitytimerfortv.ui.ProcessListViewModel
import com.exner.tools.activitytimerfortv.ui.tools.DefaultSpacer
import com.exner.tools.activitytimerfortv.ui.tools.ProcessCard
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.AboutDestination
import com.ramcosta.composedestinations.generated.destinations.CategoryListDestination
import com.ramcosta.composedestinations.generated.destinations.ManageProcessesDestination
import com.ramcosta.composedestinations.generated.destinations.ProcessDetailsDestination
import com.ramcosta.composedestinations.generated.destinations.SettingsDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

//TODO remember selected focus after navigation

@OptIn(ExperimentalTvMaterial3Api::class)
@Destination<RootGraph>(start = true)
@Composable
fun ProcessList(
    modifier: Modifier = Modifier,
    processListViewModel: ProcessListViewModel = hiltViewModel(),
    navController: DestinationsNavigator
) {
    val categoryList by processListViewModel.categoryList.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(32.dp),
        contentPadding = PaddingValues(horizontal = 48.dp, vertical = 24.dp)
    ) {
        item {
            val featuredProcessesList by
            processListViewModel.featuredProcessList.collectAsStateWithLifecycle()

            Carousel(
                itemCount = featuredProcessesList.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(376.dp),
            ) { indexOfCarouselItem ->
                val featuredProcess = featuredProcessesList[indexOfCarouselItem]
                val backgroundColour = MaterialTheme.colorScheme.background

                Box {
                    AsyncImage(
                        model = processListViewModel.getBackgroundUriForProcessOrCategory(
                            featuredProcess
                        ),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                    Box(
                        contentAlignment = Alignment.BottomStart,
                        modifier = Modifier
                            .fillMaxSize()
                            .drawBehind {
                                val brush = Brush.horizontalGradient(
                                    listOf(backgroundColour, Color.Transparent)
                                )
                                drawRect(brush)
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = featuredProcess.name,
                                style = MaterialTheme.typography.displaySmall
                            )
                            DefaultSpacer()
                            Button(onClick = {
                                navController.navigate(
                                    ProcessDetailsDestination(
                                        featuredProcess.uuid
                                    )
                                )
                            }
                            ) {
                                Text(text = "Show details")
                            }
                        }
                    }
                }
            }
        }
        items(items = categoryList, key = {it.name}) { category ->
            Text(
                text = "Category: " + category.name,
                style = MaterialTheme.typography.titleMedium
            )
            DefaultSpacer()
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.height(120.dp)
            ) {
                items(items = category.processList, key = {it.uid}) { process ->
                    ProcessCard(
                        process = process,
                        backgroundUriFallback = category.backgroundUri,
                        onClick = { navController.navigate(ProcessDetailsDestination(process.uuid)) }
                    )
                }
            }
        }
        // other stuff
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            ) {
                item {
                    Button(onClick = { navController.navigate(ManageProcessesDestination) }) {
                        Text(text = "Manage processes")
                    }
                }
                item {
                    Button(onClick = { navController.navigate(CategoryListDestination) }) {
                        Text(text = "Manage categories")
                    }
                }
                item {
                    Button(onClick = { navController.navigate(SettingsDestination) }) {
                        Text(text = "Settings")
                    }
                }
                item {
                    Button(onClick = { navController.navigate(AboutDestination) }) {
                        Text(text = "About Activity Timer for TV")
                    }
                }
            }
        }
    }
}

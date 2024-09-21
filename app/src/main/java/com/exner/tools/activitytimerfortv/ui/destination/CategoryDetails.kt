package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.exner.tools.activitytimerfortv.ui.CategoryDetailsViewModel
import com.exner.tools.activitytimerfortv.ui.DefaultSpacer
import com.exner.tools.activitytimerfortv.ui.tools.CategoryListDefinitions
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.CategoryEditDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@Destination<RootGraph>
@Composable
fun CategoryDetails(
    categoryUid: Long,
    categoryDetailsViewModel: CategoryDetailsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {

    val name by categoryDetailsViewModel.name.observeAsState()
    val backgroundUri by categoryDetailsViewModel.backgroundUri.observeAsState()
    val usage by categoryDetailsViewModel.usage.observeAsState()

    categoryDetailsViewModel.getCategory(categoryUid)

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
                // buttons
                Row {
                    if (categoryUid != CategoryListDefinitions.CATEGORY_UID_NONE) {
                        Button(
                            onClick = {
                                navigator.navigate(CategoryEditDestination(categoryUid = categoryUid))
                            },
                            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit Category",
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                            Text(text = "Edit Category")
                        }
                        Spacer(modifier = Modifier.weight(0.5f))
                        OutlinedButton(
                            onClick = {
//                            navigator.navigate(CategoryDeleteDestination(categoryUid = categoryUid))
                            },
                            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete Category"
                            )
                            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                            Text(text = "Delete Category")
                        }
                    }
                }
                // spacer
                DefaultSpacer()
                // content
                // more process information
                Column {
                    val tempName: String = name ?: ""
                    Text(text = "Category: '$tempName'")
                    var usageString = "Unused"
                    if (usage != null) {
                        usageString = "Used in ${usage!!.usageCount} processes"
                    }
                    DefaultSpacer()
                    Text(text = usageString)
                    DefaultSpacer()
                    Text(text = "Background image URL: $backgroundUri")
                }
            }
        }
    }
}
package com.exner.tools.activitytimerfortv.ui.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.NavigationDrawerScope
import androidx.tv.material3.Text
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun NavigationDrawerScope.ActivityTimerNavigationDrawerContent(
    navigator: DestinationsNavigator,
    defaultSelectedIndex: Int
) {
    var selectedItemIndex by rememberSaveable {
        mutableIntStateOf(defaultSelectedIndex)
    }
    val settingsIndex = 1 + navigationItems.size

    Column(
        Modifier
            .background(Color.Gray)
            .fillMaxHeight()
            .padding(12.dp)
            .selectableGroup(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NavigationDrawerItem(
            enabled = false,
            selected = selectedItemIndex == 0,
            onClick = {
                navigator.navigate(navigationItemAccount.destination)
                selectedItemIndex = 0
            },
            leadingContent = {
                Icon(
                    imageVector = navigationItemAccount.icon,
                    contentDescription = navigationItemAccount.description
                )
            }
        ) {
            Text(text = navigationItemAccount.title)
        }
        Spacer(modifier = Modifier.weight(0.5f))
        navigationItems.forEachIndexed { index, drawerItem ->
            NavigationDrawerItem(
                selected = selectedItemIndex == (index + 1),   // the +1 is a hack for the account menu item
                onClick = {
                    navigator.navigate(drawerItem.destination)
                    selectedItemIndex = (index + 1)
                },
                leadingContent = {
                    Icon(
                        imageVector = drawerItem.icon,
                        contentDescription = drawerItem.description
                    )
                }
            ) {
                Text(text = drawerItem.title)
            }
        }
        Spacer(modifier = Modifier.weight(0.5f))
        NavigationDrawerItem(
            selected = selectedItemIndex == settingsIndex,
            onClick = {
                navigator.navigate(navigationItemSettings.destination)
                selectedItemIndex = settingsIndex
            },
            leadingContent = {
                Icon(
                    imageVector = navigationItemSettings.icon,
                    contentDescription = navigationItemSettings.description
                )
            }
        ) {
            Text(text = navigationItemSettings.title)
        }
    }
}
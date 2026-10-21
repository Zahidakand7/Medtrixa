package com.medtrixa.app.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AppBottomNavigation(
    selected: String,
    onHomeClick: () -> Unit,
    onToolsClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        NavigationBarItem(
            selected = selected == "home",
            onClick = onHomeClick,
            icon = {
                Icon(Icons.Outlined.Home, contentDescription = "Home")
            },
            label = { Text("Home") },
            alwaysShowLabel = true
        )
        NavigationBarItem(
            selected = selected == "tools",
            onClick = onToolsClick,
            icon = {
                Icon(
                    Icons.Outlined.MedicalServices,
                    contentDescription = "Protocols"
                )
            },
            label = { Text("Protocols") },
            alwaysShowLabel = true
        )
        NavigationBarItem(
            selected = selected == "more",
            onClick = onMoreClick,
            icon = {
                Icon(Icons.Outlined.MoreHoriz, contentDescription = "More")
            },
            label = { Text("More") },
            alwaysShowLabel = true
        )
    }
}

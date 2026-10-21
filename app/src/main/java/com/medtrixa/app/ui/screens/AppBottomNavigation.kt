package com.medtrixa.app.ui.screens

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Topic
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppBottomNavigation(
    selected: String,
    onHomeClick: () -> Unit,
    onToolsClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val itemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = scheme.primary,
        selectedTextColor = scheme.primary,
        indicatorColor = scheme.primary.copy(alpha = 0.14f),
        unselectedIconColor = scheme.onSurfaceVariant,
        unselectedTextColor = scheme.onSurfaceVariant
    )
    NavigationBar(
        modifier = Modifier.height(72.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()),
        containerColor = scheme.surface
    ) {
        NavigationBarItem(
            selected = selected == "home",
            onClick = onHomeClick,
            icon = { Icon(Icons.Outlined.Dashboard, contentDescription = "Toolkit") },
            label = { Text("Toolkit") },
            alwaysShowLabel = true,
            colors = itemColors
        )
        NavigationBarItem(
            selected = selected == "tools",
            onClick = onToolsClick,
            icon = { Icon(Icons.Outlined.MedicalServices, contentDescription = "Protocols") },
            label = { Text("Protocols") },
            alwaysShowLabel = true,
            colors = itemColors
        )
        NavigationBarItem(
            selected = selected == "more",
            onClick = onMoreClick,
            icon = { Icon(Icons.Outlined.Topic, contentDescription = "Topics") },
            label = { Text("Topics") },
            alwaysShowLabel = true,
            colors = itemColors
        )
    }
}

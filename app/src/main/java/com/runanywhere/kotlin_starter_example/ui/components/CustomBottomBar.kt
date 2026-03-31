package com.runanywhere.kotlin_starter_example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun CustomBottomBar(
    navController: NavController,
    onHomeClick: () -> Unit,
    onTasksClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onShareClick: () -> Unit
) {
    // Dynamically calculate the selected item based on the current route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selectedItem = when {
        currentRoute == "dashboard" -> 0
        currentRoute == "tasks" -> 1
        currentRoute == "settings" -> 2
        currentRoute == "community" -> 3
        else -> 0 // Default or handle other nested routes
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFD1C4E9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            BottomBarIcon(
                icon = Icons.Default.Home,
                isSelected = selectedItem == 0
            ) {
                onHomeClick()
            }

            BottomBarIcon(
                icon = Icons.Default.List,
                isSelected = selectedItem == 1
            ) {
                onTasksClick()
            }

            BottomBarIcon(
                icon = Icons.Default.Settings,
                isSelected = selectedItem == 2
            ) {
                onSettingsClick()
            }

            BottomBarIcon(
                icon = Icons.Default.Share,
                isSelected = selectedItem == 3
            ) {
                onShareClick()
            }
        }
    }
}

@Composable
fun BottomBarIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(if (isSelected) Color(0xFFB39DDB) else Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = if (isSelected) Color(0xFF4A148C) else Color(0xFF7E57C2)
            )
        }
    }
}

package com.runanywhere.kotlin_starter_example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.runanywhere.kotlin_starter_example.R
import androidx.navigation.NavController
import com.runanywhere.kotlin_starter_example.ui.theme.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person

@Composable
fun NeuroTopBar(navController: NavController? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LavenderShell)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "NEURO NEXUS", 
            color = AccentPurple,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = {}) {
            Icon(Icons.Default.Notifications, contentDescription = null, tint = AccentPurple)
        }

        IconButton(
            onClick = {
                navController?.navigate("profile")
            }
        ) {
            Icon(Icons.Default.Person, contentDescription = "Profile", tint = AccentPurple)
        }
    }
}

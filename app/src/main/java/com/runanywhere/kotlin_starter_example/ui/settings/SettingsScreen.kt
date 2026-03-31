package com.runanywhere.kotlin_starter_example.ui.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.runanywhere.kotlin_starter_example.ui.components.CustomBottomBar
import com.runanywhere.kotlin_starter_example.ui.components.NeuroTopBar
import androidx.compose.ui.res.stringResource
import com.runanywhere.kotlin_starter_example.R

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var showLanguageDialog by remember { mutableStateOf(false) }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(stringResource(R.string.language)) },
            text = {
                Column {
                    ListItem(
                        headlineContent = { Text("English") },
                        modifier = Modifier.clickable { 
                            viewModel.setLanguage("en")
                            showLanguageDialog = false
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Hindi (हिंदी)") },
                        modifier = Modifier.clickable { 
                            viewModel.setLanguage("hi")
                            showLanguageDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = { NeuroTopBar(navController) },
        bottomBar = {
            CustomBottomBar(
                navController = navController,
                onHomeClick = { navController.navigate("dashboard") },
                onTasksClick = { navController.navigate("tasks") },
                onSettingsClick = { /* Already here */ },
                onShareClick = { navController.navigate("community") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF3E5F5))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            // Preferences Section
            SettingsSection(title = stringResource(R.string.preferences)) {
                Text(
                    text = stringResource(R.string.display_text_size),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                SingleSelectSegmentedButton(
                    options = listOf(
                        stringResource(R.string.standard),
                        stringResource(R.string.enhanced),
                        stringResource(R.string.maximised)
                    ),
                    selectedOption = when(state.textSize) {
                        TextSizeConfig.Standard -> stringResource(R.string.standard)
                        TextSizeConfig.Enhanced -> stringResource(R.string.enhanced)
                        TextSizeConfig.Maximised -> stringResource(R.string.maximised)
                    },
                    onOptionSelected = { option ->
                        val config = when(option) {
                            context.getString(R.string.standard) -> TextSizeConfig.Standard
                            context.getString(R.string.enhanced) -> TextSizeConfig.Enhanced
                            context.getString(R.string.maximised) -> TextSizeConfig.Maximised
                            else -> TextSizeConfig.Standard
                        }
                        viewModel.setTextSize(config)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.theme),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.dark_mode))
                    Switch(
                        checked = state.isDarkTheme,
                        onCheckedChange = { viewModel.toggleTheme(it) }
                    )
                }

                SettingsItem(
                    icon = Icons.Default.Language,
                    title = "${stringResource(R.string.language)} (${if(state.language == "hi") "Hindi" else "English"})",
                    onClick = { showLanguageDialog = true }
                )
            }

            // Notification and Alerts Section
            SettingsSection(title = stringResource(R.string.notification_alerts)) {
                SettingsItem(icon = Icons.Default.Notifications, title = stringResource(R.string.alerts), onClick = { navController.navigate("alerts") })
                SettingsItem(icon = Icons.Default.History, title = stringResource(R.string.activity), onClick = { navController.navigate("activity") })
                SettingsItem(icon = Icons.AutoMirrored.Filled.Message, title = stringResource(R.string.messages), onClick = { navController.navigate("messages_settings") })
            }

            // Support Section
            SettingsSection(title = stringResource(R.string.support)) {
                SettingsItem(icon = Icons.AutoMirrored.Filled.Help, title = stringResource(R.string.help_centre), onClick = { navController.navigate("help") })
                SettingsItem(icon = Icons.Default.ContactSupport, title = stringResource(R.string.contact_support), onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:support@neuronexus.com")
                    }
                    context.startActivity(intent)
                })
                SettingsItem(icon = Icons.Default.Feedback, title = stringResource(R.string.feedback), onClick = { navController.navigate("feedback") })
            }

            // Privacy and Security Section
            SettingsSection(title = stringResource(R.string.privacy_security)) {
                SettingsItem(icon = Icons.Default.Security, title = stringResource(R.string.permissions), onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                })
                SettingsItem(icon = Icons.Default.Storage, title = stringResource(R.string.storage_settings), onClick = { navController.navigate("storage_settings") })
            }

            // Logout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        viewModel.logout()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color.Red)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = stringResource(R.string.logout), color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF7E57C2),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFF7E57C2))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = title, fontSize = 16.sp)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
    }
}

@Composable
fun SingleSelectSegmentedButton(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF3E5F5))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        options.forEach { option ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (option == selectedOption) Color.White else Color.Transparent)
                    .clickable { onOptionSelected(option) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    fontWeight = if (option == selectedOption) FontWeight.Bold else FontWeight.Normal,
                    color = if (option == selectedOption) Color(0xFF7E57C2) else Color.Gray
                )
            }
        }
    }
}

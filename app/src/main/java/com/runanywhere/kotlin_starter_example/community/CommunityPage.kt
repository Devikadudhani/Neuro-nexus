package com.runanywhere.kotlin_starter_example.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.runanywhere.kotlin_starter_example.R
import com.runanywhere.kotlin_starter_example.ui.components.CustomBottomBar
import com.runanywhere.kotlin_starter_example.ui.components.NeuroTopBar

@Composable
fun CommunityPage(
    navController: NavController
) {
    Scaffold(
        topBar = { NeuroTopBar(navController) },
        bottomBar = {
            CustomBottomBar(
                navController = navController,
                onHomeClick = { navController.navigate("dashboard") },
                onTasksClick = { navController.navigate("tasks") },
                onSettingsClick = { navController.navigate("settings") },
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
                .padding(20.dp)
        ) {

            Text(
                text = stringResource(R.string.community),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            SectionHeader(stringResource(R.string.featured_articles))
            ArticleCard(
                "Dementia Is Not Inevitable. How to Lower Your Risk.",
                "Early detection of diseases causing dementia using digital technologies."
            )
            ArticleCard(
                "New Studies Identify Early Warning Signs of Dementia",
                "Brain scans and AI models can predict cognitive decline."
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionHeader(stringResource(R.string.faqs))
            FaqItem(
                "What is dementia?",
                "Dementia is a loss of thinking, remembering, and reasoning skills severe enough to interfere with daily life."
            )
            FaqItem(
                "Is dementia more common in older people?",
                "Yes, age is the strongest risk factor, but it is not inevitable."
            )
            FaqItem(
                "What are early signs of dementia?",
                "Memory loss, confusion with time or place, and difficulty completing familiar tasks."
            )
            FaqItem(
                "Can dementia be prevented or slowed?",
                "Healthy lifestyle habits and early detection can help manage and slow progression."
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionHeader(stringResource(R.string.contacts))
            ContactItem("Dementia India Alliance Helpline", "8585 990 990")
            ContactItem("ARDSI Delhi Chapter", "+91 93154 18060")
            ContactItem("Tele-MANAS Mental Health Helpline", "14416 / 1-800-91-4416")
            ContactItem("Dr. Manjari Tripathi (AIIMS Delhi)", "+91-11-2658 8500")
            ContactItem("Dr. Shubha Mittal (Sir Ganga Ram Hospital)", "+91-11-25750000")

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(stringResource(R.string.see_more) + " >", fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun ArticleCard(title: String, description: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color(0xFFE0D7FF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold)
            Text(description, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun FaqItem(question: String, answer: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Q. $question", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(answer, fontSize = 13.sp, color = Color.DarkGray)
        }
    }
}

@Composable
fun ContactItem(name: String, info: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFD1C4E9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(name, fontWeight = FontWeight.Bold)
                Text(info, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

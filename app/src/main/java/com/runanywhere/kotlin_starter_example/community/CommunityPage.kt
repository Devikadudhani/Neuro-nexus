package com.runanywhere.kotlin_starter_example.community

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.runanywhere.kotlin_starter_example.R
import com.runanywhere.kotlin_starter_example.ui.components.CustomBottomBar
import com.runanywhere.kotlin_starter_example.ui.components.NeuroTopBar
import com.runanywhere.kotlin_starter_example.ui.theme.*
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.public.extensions.chat
import kotlinx.coroutines.launch

@Composable
fun CommunityPage(navController: NavController) {
    val tips = listOf(
        "Take a short walk outside today — even ten minutes of sunlight can meaningfully shift your mood.",
        "It's okay to ask for help. Reaching out is one of the most courageous things a caregiver can do.",
        "Celebrate small wins — a good conversation, a shared laugh. These moments are real and they matter.",
        "You cannot pour from an empty cup. Rest is not selfish; it is how you keep showing up.",
        "Write down one thing that went well today, no matter how small. Your effort is making a difference."
    )
    var tipIndex by remember { mutableStateOf(0) }
    var aiTip by remember { mutableStateOf(tips[0]) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                aiTip = RunAnywhere.chat(
                    "Give me a single warm, human sentence of support for someone caring for a loved one with dementia. No quotes, no preamble."
                )
            } catch (_: Exception) { }
        }
    }

    Scaffold(
        topBar = { NeuroTopBar(navController) },
        containerColor = LavenderShell,
        bottomBar = {
            CustomBottomBar(
                navController = navController,
                onHomeClick      = { navController.navigate("dashboard") },
                onTasksClick     = { navController.navigate("tasks") },
                onSettingsClick  = { navController.navigate("settings") },
                onShareClick     = { navController.navigate("community") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(LavenderShell)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            // Page header
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.community),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentPurple,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Support, stories & resources for caregivers",
                    fontSize = 14.sp,
                    color = Ink.copy(alpha = 0.6f)
                )
            }

            // Daily tip card
            DailyTipCard(
                tip = aiTip,
                onRefresh = {
                    tipIndex = (tipIndex + 1) % tips.size
                    aiTip = tips[tipIndex]
                }
            )

            // Featured articles
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                SectionHeader(stringResource(R.string.featured_articles))
                FeaturedArticlesRow()
            }

            // FAQs
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionHeader(stringResource(R.string.faqs))
                ExpandableFaqItem(
                    "What exactly is dementia?",
                    "Dementia is an umbrella term — like heart disease — covering a range of specific conditions including Alzheimer's. It's not a single disease but a set of symptoms affecting memory, thinking, and daily life."
                )
                ExpandableFaqItem(
                    "What are the early warning signs?",
                    "Common early markers include subtle memory lapses, difficulty planning or problem-solving, confusion with time and place, and shifts in mood or speech. These are often gradual and easy to overlook."
                )
                ExpandableFaqItem(
                    "Can dementia be prevented?",
                    "While some genetic factors can't be changed, regular physical activity, mental stimulation, quality sleep, and a balanced diet can meaningfully lower your risk over time."
                )
            }

            // Support contacts
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionHeader(stringResource(R.string.contacts))
                CallContactItem("Dementia India Alliance", "8585 990 990")
                CallContactItem("ARDSI Delhi Chapter", "93154 18060")
                CallContactItem("Tele-MANAS Helpline", "14416")
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun DailyTipCard(tip: String, onRefresh: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AccentPurple)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(AccentPurple, LavenderHeader)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "DAILY NEURO-TIP",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "\u201C$tip\u201D",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    lineHeight = 24.sp
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .clickable { onRefresh() }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.Refresh,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "New tip",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

private data class Article(val tag: String, val title: String, val desc: String, val readTime: String)

@Composable
fun FeaturedArticlesRow() {
    val articles = listOf(
        Article("Research",   "Lowering Your Risk Early",     "Digital markers for early detection of cognitive decline.", "4 min"),
        Article("AI & Brain", "How Models Predict Decline",   "New research on AI and cognitive health forecasting.",      "6 min"),
        Article("Nutrition",  "Foods for a Sharper Mind",     "What to eat to support long-term cognitive health.",        "5 min")
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(end = 4.dp)
    ) {
        items(articles) { article ->
            Card(
                modifier = Modifier
                    .width(220.dp)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, AccentMuted)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = AccentMuted
                    ) {
                        Text(
                            text = article.tag.uppercase(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentPurple,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = article.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Ink,
                        lineHeight = 22.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(
                        text = article.desc,
                        fontSize = 13.sp,
                        color = Ink.copy(alpha = 0.6f),
                        lineHeight = 18.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${article.readTime} read",
                            fontSize = 11.sp,
                            color = AccentPurple.copy(alpha = 0.6f)
                        )
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(AccentMuted),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = "Read article",
                                tint = AccentPurple,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandableFaqItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (expanded) 180f else 0f, label = "chevron")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, AccentMuted)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ink,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Default.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(rotation),
                    tint = AccentPurple
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    HorizontalDivider(color = AccentMuted)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = answer,
                        fontSize = 14.sp,
                        color = Ink.copy(alpha = 0.8f),
                        lineHeight = 22.sp
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun CallContactItem(name: String, number: String) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, AccentMuted)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AccentMuted),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.SupportAgent,
                    contentDescription = null,
                    tint = AccentPurple,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = number,
                    fontSize = 13.sp,
                    color = AccentPurple.copy(alpha = 0.7f)
                )
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = AccentPurple,
                modifier = Modifier.clickable {
                    context.startActivity(
                        Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
                    )
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Rounded.Call,
                        contentDescription = "Call",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Call",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = AccentPurple,
            letterSpacing = (-0.3).sp
        )
        TextButton(onClick = { }) {
            Text(
                text = stringResource(R.string.see_more),
                fontSize = 13.sp,
                color = AccentPurple,
                fontWeight = FontWeight.Bold
            )
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = AccentPurple
            )
        }
    }
}

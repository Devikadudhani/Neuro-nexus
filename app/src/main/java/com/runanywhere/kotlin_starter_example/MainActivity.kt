package com.runanywhere.kotlin_starter_example

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

import com.runanywhere.kotlin_starter_example.services.ModelService
import com.runanywhere.kotlin_starter_example.ui.theme.NeuroNexusTheme
import com.runanywhere.kotlin_starter_example.ui.auth.*
import com.runanywhere.kotlin_starter_example.ui.dashboard.NeuroNexusDashboard
import com.runanywhere.kotlin_starter_example.ui.tasks.*
import com.runanywhere.kotlin_starter_example.community.CommunityPage
import com.runanywhere.kotlin_starter_example.ui.VoiceTaskScreen
import com.runanywhere.kotlin_starter_example.ui.VoiceTaskViewModel
import com.runanywhere.kotlin_starter_example.ui.profile.ProfileScreen
import com.runanywhere.kotlin_starter_example.ui.settings.*
import com.runanywhere.kotlin_starter_example.face.FaceAnalysisScreen
import com.runanywhere.kotlin_starter_example.ui.reports.*
import com.runanywhere.sdk.core.onnx.ONNX
import com.runanywhere.sdk.foundation.bridge.extensions.CppBridgeModelPaths
import com.runanywhere.sdk.llm.llamacpp.LlamaCPP
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.public.SDKEnvironment
import com.runanywhere.sdk.storage.AndroidPlatformContext
import com.runanywhere.kotlin_starter_example.tasks.MemoryPreviewScreen
import com.runanywhere.kotlin_starter_example.ui.tasks.RecallResultScreen
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val voiceViewModel: VoiceTaskViewModel by viewModels {
        VoiceTaskViewModel.Factory(application)
    }

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
        val lang = prefs.getString("language", "en") ?: "en"
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = newBase.resources.configuration
        config.setLocale(locale)
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        AndroidPlatformContext.initialize(this)
        RunAnywhere.initialize(environment = SDKEnvironment.DEVELOPMENT)

        val runanywherePath = java.io.File(filesDir, "runanywhere").absolutePath
        CppBridgeModelPaths.setBaseDirectory(runanywherePath)

        try {
            LlamaCPP.register(priority = 100)
        } catch (e: Throwable) {
            Log.w("MainActivity", "LlamaCPP.register partial failure: ${e.message}")
        }
        ONNX.register(priority = 100)
        ModelService.registerDefaultModels()

        setContent {
            val settingsViewModel: SettingsViewModel =
                viewModel(viewModelStoreOwner = this@MainActivity)

            val settingsState by settingsViewModel.state.collectAsState()
            val context = LocalContext.current

            LaunchedEffect(settingsState.language) {
                if (settingsState.shouldRestartActivity) {
                    settingsViewModel.onActivityRestarted()
                    (context as? Activity)?.recreate()
                }
            }

            val fontScale = when (settingsState.textSize) {
                TextSizeConfig.Standard -> 1.0f
                TextSizeConfig.Enhanced -> 1.25f
                TextSizeConfig.Maximised -> 1.5f
            }

            CompositionLocalProvider(
                LocalDensity provides Density(
                    density = LocalDensity.current.density,
                    fontScale = fontScale
                )
            ) {
                NeuroNexusTheme {
                    RunAnywhereApp(settingsViewModel, voiceViewModel)
                }
            }
        }
    }
}

@Composable
fun RunAnywhereApp(
    settingsViewModel: SettingsViewModel,
    voiceViewModel: VoiceTaskViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "welcome") {

        // ---------- AUTH ----------
        composable("welcome") { WelcomeScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("role_select") { RoleSelectionScreen(navController) }
        composable("signup_personal") { SignupPersonalScreen(navController) }
        composable("signup_contact") { SignupContactScreen(navController) }
        composable("signup_health") { SignupHealthScreen(navController) }
        composable("signup_profile") { SignupProfileScreen(navController) }
        composable("avatar_select") { AvatarSelectionScreen(navController) }
        composable("loading") { LoadingScreen(navController) }

        // ---------- DASHBOARD ----------
        composable("dashboard") {
            NeuroNexusDashboard(
                navController = navController,
                onHomeClick = { navController.navigate("dashboard") },
                onTasksClick = { navController.navigate("tasks") },
                onSettingsClick = { navController.navigate("settings") },
                onShareClick = { navController.navigate("community") }
            )
        }

        composable("settings") {
            SettingsScreen(navController, viewModel = settingsViewModel)
        }

        composable("tasks") { TasksScreen(navController) }
        composable("community") { CommunityPage(navController) }
        composable("profile") { ProfileScreen(navController) }

        // ---------- VOICE ----------
        composable("voice_task") {
            VoiceTaskScreen(navController = navController, viewModel = voiceViewModel)
        }

        // ---------- FACE ----------
        composable("face_analysis") {
            FaceAnalysisScreen(navController = navController)
        }

        // ---------- MEMORY ----------
        composable("memory_match") { MemoryMatchScreen(navController) }
        composable("memory_mcq") { MemoryMcqScreen(navController) }
        composable("memory_preview") { MemoryPreviewScreen(navController) }
        composable("memory_recall") { MemoryRecallScreen(navController) }

        composable(
            route = "memory_score/{score}",
            arguments = listOf(navArgument("score") { type = NavType.IntType })
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            MemoryScoreScreen(navController, score)
        }

        // ---------- STORY RECALL ----------
        composable("story") { StoryScreen(navController) }
        composable("recall_phase") { RecallPhaseScreen(navController) }
        composable("recall_question") { RecallQuestionScreen(navController) }

        composable("recall_result/{score}/{time}") { backStackEntry ->
            val score = backStackEntry.arguments?.getString("score")?.toInt() ?: 0
            val time = backStackEntry.arguments?.getString("time")?.toInt() ?: 0
            RecallResultScreen(navController, score, time)
        }

        // ---------- DUAL ----------
        composable("dual_test_intro") { DualTestIntroScreen(navController) }
        composable("dual_test_game") { DualTestGameScreen(navController) }
        composable("dual_test_result/{score}") { backStackEntry ->
            val score = backStackEntry.arguments?.getString("score")!!.toInt()
            DualTestResultScreen(navController, score)
        }

        // ---------- NBACK ----------
        composable("nback_intro") { NBackIntroScreen(navController) }
        composable("nback_game") { NBackGameScreen(navController) }
        composable("nback_result/{score}/{errors}/{avgTime}") { backStackEntry ->
            val score = backStackEntry.arguments?.getString("score")!!.toInt()
            val errors = backStackEntry.arguments?.getString("errors")!!.toInt()
            val avgTime = backStackEntry.arguments?.getString("avgTime")!!.toFloat()
            NBackResultScreen(navController, score, errors, avgTime)
        }

        // ---------- STROOP ----------
        composable("stroop_intro") { StroopIntroScreen(navController) }
        composable("stroop_game") { StroopGameScreen(navController) }
        composable("stroop_result/{score}/{time}") { backStackEntry ->
            val score = backStackEntry.arguments?.getString("score")?.toInt() ?: 0
            val time = backStackEntry.arguments?.getString("time")?.toInt() ?: 0
            StroopResultScreen(navController, score, time)
        }

        // ---------- REPORTS ----------
        composable("reports") { ReportsScreen(navController) }
        composable("report_speech") { SpeechReportScreen(navController) }
        composable("report_face") { FacialReportScreen(navController) }
        composable("report_cognitive") { CognitiveReportScreen(navController) }
    }
}
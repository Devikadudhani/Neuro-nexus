package com.runanywhere.kotlin_starter_example.ui.settings

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class TextSizeConfig {
    Standard, Enhanced, Maximised
}

data class SettingsState(
    val textSize: TextSizeConfig = TextSizeConfig.Standard,
    val isDarkTheme: Boolean = false,
    val language: String = "en",
    val shouldRestartActivity: Boolean = false
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(
        SettingsState(
            textSize = TextSizeConfig.valueOf(prefs.getString("text_size", TextSizeConfig.Standard.name) ?: TextSizeConfig.Standard.name),
            isDarkTheme = prefs.getBoolean("is_dark_theme", false),
            language = prefs.getString("language", "en") ?: "en"
        )
    )
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    fun setTextSize(size: TextSizeConfig) {
        prefs.edit().putString("text_size", size.name).apply()
        _state.value = _state.value.copy(textSize = size)
    }

    fun toggleTheme(isDark: Boolean) {
        prefs.edit().putBoolean("is_dark_theme", isDark).apply()
        _state.value = _state.value.copy(isDarkTheme = isDark)
    }

    fun setLanguage(lang: String) {
        prefs.edit().putString("language", lang).apply()
        
        // Update locale immediately for the current process
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = getApplication<Application>().resources.configuration
        config.setLocale(locale)
        getApplication<Application>().resources.updateConfiguration(config, getApplication<Application>().resources.displayMetrics)
        
        _state.value = _state.value.copy(language = lang, shouldRestartActivity = true)
    }
    
    fun onActivityRestarted() {
        _state.value = _state.value.copy(shouldRestartActivity = false)
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}

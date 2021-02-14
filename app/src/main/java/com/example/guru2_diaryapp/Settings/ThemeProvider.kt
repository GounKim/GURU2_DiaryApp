package com.example.guru2_diaryapp.Settings

import android.app.UiModeManager
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.example.guru2_diaryapp.R
import java.security.InvalidParameterException

// 테마 변경 파일
class ThemeProvider(private val context: Context) {
    fun getThemeFromPreferences(): Int {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val selectedTheme = sharedPreferences.getString(
                context.getString(R.string.theme_preferences_key),""
        )

        return selectedTheme?.let {
            getTheme(it)
        } ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }
    fun getThemeDescriptionForPreference(preferenceValue: String?): String =
        when (preferenceValue) {
            context.getString(R.string.dark_theme_preference_value) -> context.getString(R.string.dark_theme_description)
            "쿠키" -> "쿠키"
            else -> context.getString(R.string.cookie_theme_description)
        }

    fun getTheme(selectedTheme: String): Int = when (selectedTheme) {
        context.getString(R.string.dark_theme_preference_value) -> UiModeManager.MODE_NIGHT_YES
        context.getString(R.string.cookie_theme_preference_value) -> UiModeManager.MODE_NIGHT_NO
        else -> throw InvalidParameterException("Theme not defined for $selectedTheme")
    }
}
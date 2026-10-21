package org.umn.where2go

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.umn.where2go.navigation.AppNav
import org.umn.where2go.ui.theme.Where2GoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferences = getSharedPreferences(
            "where2go_settings",
            MODE_PRIVATE
        )

        var darkTheme by mutableStateOf(
            preferences.getBoolean("dark_theme", false)
        )

        setContent {
            Where2GoTheme(darkTheme = darkTheme) {
                AppNav(
                    darkTheme = darkTheme,
                    onThemeChange = { isDark ->
                        darkTheme = isDark
                        preferences.edit()
                            .putBoolean("dark_theme", isDark)
                            .apply()
                    }
                )
            }
        }
    }
}
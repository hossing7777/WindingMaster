package com.windingmaster.app

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.os.LocaleListCompat
import androidx.navigation.compose.rememberNavController
import com.windingmaster.app.presentation.navigation.MainNavigation
import com.windingmaster.app.presentation.theme.WindingMasterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // بارگذاری زبان ذخیره‌شده
        val prefs = getSharedPreferences("winding_master_prefs", Context.MODE_PRIVATE)
        val lang = prefs.getString("app_language", "fa") ?: "fa"
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(lang))

        val isRtl = lang == "fa"

        setContent {
            CompositionLocalProvider(
                LocalLayoutDirection provides if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
            ) {
                WindingMasterTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()
                        MainNavigation(navController = navController)
                    }
                }
            }
        }
    }
}

package com.windingmaster.app.presentation.screens.settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
@Composable
fun SettingsScreen() { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("تنظیمات - نسخه ۱.۰") } }
@Composable
fun PlaceholderScreen(text: String) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(text) } }

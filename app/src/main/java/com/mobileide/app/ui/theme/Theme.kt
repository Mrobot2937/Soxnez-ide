package com.mobileide.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val IDEColorScheme = darkColorScheme(
    background = EditorBackground,
    surface = SidebarBackground,
    primary = AccentBlue,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

@Composable
fun MobileIDETheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = IDEColorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}

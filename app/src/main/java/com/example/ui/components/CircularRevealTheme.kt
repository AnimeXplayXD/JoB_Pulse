package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.theme.ThemeRevealProvider

// Re-export typealiases and CompositionLocals for backward compatibility
typealias ThemeWaveState = com.example.theme.ThemeWaveState
typealias CircularRevealController = com.example.theme.ThemeRevealController

val LocalThemeWave = com.example.theme.LocalThemeWave
val LocalCircularReveal = com.example.theme.LocalThemeRevealController

/**
 * Backward-compatible wrapper that delegates to [ThemeRevealProvider].
 */
@Composable
fun CircularRevealTheme(
    onToggleTheme: () -> Unit,
    isDarkTheme: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    ThemeRevealProvider(
        onToggleTheme = onToggleTheme,
        isDarkTheme = isDarkTheme,
        modifier = modifier,
        content = content
    )
}

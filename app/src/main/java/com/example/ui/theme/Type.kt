package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private fun textStyle(size: Int, height: Int, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = FontFamily.Default, fontSize = size.sp, lineHeight = height.sp,
    fontWeight = weight, letterSpacing = 0.sp
)

val Typography = Typography(
    displayLarge = textStyle(34, 42, FontWeight.SemiBold),
    displayMedium = textStyle(30, 38, FontWeight.SemiBold),
    displaySmall = textStyle(28, 36, FontWeight.SemiBold),
    headlineLarge = textStyle(26, 34, FontWeight.SemiBold),
    headlineMedium = textStyle(24, 32, FontWeight.SemiBold),
    headlineSmall = textStyle(22, 30, FontWeight.SemiBold),
    titleLarge = textStyle(20, 28, FontWeight.SemiBold),
    titleMedium = textStyle(17, 24, FontWeight.Medium),
    titleSmall = textStyle(15, 22, FontWeight.Medium),
    bodyLarge = textStyle(16, 24), bodyMedium = textStyle(15, 22), bodySmall = textStyle(13, 20),
    labelLarge = textStyle(14, 20, FontWeight.Medium),
    labelMedium = textStyle(13, 18, FontWeight.Medium),
    labelSmall = textStyle(12, 18, FontWeight.Medium)
)

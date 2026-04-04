package org.example.project.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

// ─── Paleta Dark ──────────────────────────────────────────────────────────────
object DarkColors {
    val Background      = Color(0xFF060608)
    val Surface         = Color(0xFF12121C)
    val GlassWhite      = Color(0x0FFFFFFF)
    val GlassBorder     = Color(0x18FFFFFF)
    val PurpleCore      = Color(0xFF7C3AED)
    val PurpleGlow      = Color(0xFF9F67FF)
    val PurpleNeon      = Color(0xFFBF9FFF)
    val PurpleDim       = Color(0xFF2D1B5E)
    val PurpleBright    = Color(0xFFAB47FF)
    val CrimsonGlow     = Color(0xFFE53935)
    val EmeraldGlow     = Color(0xFF10B981)
    val GoldShine       = Color(0xFFFFBD2E)
    val SilverText      = Color(0xFFE2E2F0)
    val DimText         = Color(0xFF6B6B8A)
    val GhostText       = Color(0xFF35354A)
    val CardBg          = Color(0xFF12121C)
    val AmbientBlob1    = Color(0xFF7C3AED)
    val AmbientBlob2    = Color(0xFF1A0A4E)
    val HeroGradientEnd = Color(0xFFCCBBFF)
    val ScanGradStart   = Color(0xFF9B30FF)
    val ScanGradEnd     = Color(0xFF6B21D6)
    val ScanGlowBorder  = Color(0xFFBF7FFF)
    val UploadIconBg    = Color(0xFF2D1B5E)
    val NavBarBg        = Color(0xFF12121C)
    val SheetOptionBg   = Color(0xFF1C1C28)
    val NavBorderLine   = Color(0x18FFFFFF)
}

// ─── Paleta Light ─────────────────────────────────────────────────────────────
object LightColors {
    val Background      = Color(0xFFF4F2FF)
    val Surface         = Color(0xFFFFFFFF)
    val GlassWhite      = Color(0x18000000)
    val GlassBorder     = Color(0x28000000)
    val PurpleCore      = Color(0xFF7C3AED)
    val PurpleGlow      = Color(0xFF7C3AED)
    val PurpleNeon      = Color(0xFF6D28D9)
    val PurpleDim       = Color(0xFFEDE9FE)
    val PurpleBright    = Color(0xFFAB47FF)
    val CrimsonGlow     = Color(0xFFDC2626)
    val EmeraldGlow     = Color(0xFF059669)
    val GoldShine       = Color(0xFFD97706)
    val SilverText      = Color(0xFF1E1B4B)
    val DimText         = Color(0xFF6D6B8A)
    val GhostText       = Color(0xFFAFADCA)
    val CardBg          = Color(0xFFFFFFFF)
    val AmbientBlob1    = Color(0xFF7C3AED)
    val AmbientBlob2    = Color(0xFF5B21B6)
    val HeroGradientEnd = Color(0xFF6D28D9)
    val ScanGradStart   = Color(0xFF7C3AED)
    val ScanGradEnd     = Color(0xFF5B21B6)
    val ScanGlowBorder  = Color(0xFF9F67FF)
    val UploadIconBg    = Color(0xFFEDE9FE)
    val NavBarBg        = Color(0xFFFFFFFF)
    val SheetOptionBg   = Color(0xFFF8F7FF)
    val NavBorderLine   = Color(0x28000000)
}

// ─── AppColors — wrapper animat ───────────────────────────────────────────────
data class AppColors(
    val background: Color,
    val surface: Color,
    val glassWhite: Color,
    val glassBorder: Color,
    val purpleCore: Color,
    val purpleGlow: Color,
    val purpleNeon: Color,
    val purpleDim: Color,
    val purpleBright: Color,
    val crimsonGlow: Color,
    val emeraldGlow: Color,
    val goldShine: Color,
    val silverText: Color,
    val dimText: Color,
    val ghostText: Color,
    val cardBg: Color,
    val ambientBlob1: Color,
    val ambientBlob2: Color,
    val heroGradientEnd: Color,
    val scanGradStart: Color,
    val scanGradEnd: Color,
    val scanGlowBorder: Color,
    val uploadIconBg: Color,
    val navBarBg: Color,
    val sheetOptionBg: Color,
    val navBorderLine: Color,
    val isDark: Boolean
)

// ─── CompositionLocal ─────────────────────────────────────────────────────────
val LocalAppColors = compositionLocalOf {
    buildColors(isDark = true)
}

val LocalIsDarkTheme = compositionLocalOf { true }

fun buildColors(isDark: Boolean): AppColors {
    return if (isDark) {
        AppColors(
            background      = DarkColors.Background,
            surface         = DarkColors.Surface,
            glassWhite      = DarkColors.GlassWhite,
            glassBorder     = DarkColors.GlassBorder,
            purpleCore      = DarkColors.PurpleCore,
            purpleGlow      = DarkColors.PurpleGlow,
            purpleNeon      = DarkColors.PurpleNeon,
            purpleDim       = DarkColors.PurpleDim,
            purpleBright    = DarkColors.PurpleBright,
            crimsonGlow     = DarkColors.CrimsonGlow,
            emeraldGlow     = DarkColors.EmeraldGlow,
            goldShine       = DarkColors.GoldShine,
            silverText      = DarkColors.SilverText,
            dimText         = DarkColors.DimText,
            ghostText       = DarkColors.GhostText,
            cardBg          = DarkColors.CardBg,
            ambientBlob1    = DarkColors.AmbientBlob1,
            ambientBlob2    = DarkColors.AmbientBlob2,
            heroGradientEnd = DarkColors.HeroGradientEnd,
            scanGradStart   = DarkColors.ScanGradStart,
            scanGradEnd     = DarkColors.ScanGradEnd,
            scanGlowBorder  = DarkColors.ScanGlowBorder,
            uploadIconBg    = DarkColors.UploadIconBg,
            navBarBg        = DarkColors.NavBarBg,
            sheetOptionBg   = DarkColors.SheetOptionBg,
            navBorderLine   = DarkColors.NavBorderLine,
            isDark          = true
        )
    } else {
        AppColors(
            background      = LightColors.Background,
            surface         = LightColors.Surface,
            glassWhite      = LightColors.GlassWhite,
            glassBorder     = LightColors.GlassBorder,
            purpleCore      = LightColors.PurpleCore,
            purpleGlow      = LightColors.PurpleGlow,
            purpleNeon      = LightColors.PurpleNeon,
            purpleDim       = LightColors.PurpleDim,
            purpleBright    = LightColors.PurpleBright,
            crimsonGlow     = LightColors.CrimsonGlow,
            emeraldGlow     = LightColors.EmeraldGlow,
            goldShine       = LightColors.GoldShine,
            silverText      = LightColors.SilverText,
            dimText         = LightColors.DimText,
            ghostText       = LightColors.GhostText,
            cardBg          = LightColors.CardBg,
            ambientBlob1    = LightColors.AmbientBlob1,
            ambientBlob2    = LightColors.AmbientBlob2,
            heroGradientEnd = LightColors.HeroGradientEnd,
            scanGradStart   = LightColors.ScanGradStart,
            scanGradEnd     = LightColors.ScanGradEnd,
            scanGlowBorder  = LightColors.ScanGlowBorder,
            uploadIconBg    = LightColors.UploadIconBg,
            navBarBg        = LightColors.NavBarBg,
            sheetOptionBg   = LightColors.SheetOptionBg,
            navBorderLine   = LightColors.NavBorderLine,
            isDark          = false
        )
    }
}

// ─── AppTheme Wrapper ─────────────────────────────────────────────────────────
@Composable
fun AppTheme(
    isDark: Boolean,
    content: @Composable () -> Unit
) {
    val colors = buildColors(isDark)
    CompositionLocalProvider(
        LocalAppColors provides colors,
        LocalIsDarkTheme provides isDark
    ) {
        content()
    }
}

// ─── Shortcut ─────────────────────────────────────────────────────────────────
val appColors: AppColors
    @Composable get() = LocalAppColors.current
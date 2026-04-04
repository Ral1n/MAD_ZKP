package org.example.project.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.example.project.theme.appColors

// ─── HomeScreen ───────────────────────────────────────────────────────────────
@Composable
fun HomeScreen(
    onNavigateToHistory: () -> Unit = {},
    onNavigateToFiles: () -> Unit = {},
    isDarkTheme: Boolean = true,
    onToggleTheme: () -> Unit = {}
) {
    val c = appColors

    val openCamera  = rememberCameraLauncher  { _ -> }
    val openGallery = rememberGalleryLauncher { _ -> }
    val openFiles   = rememberFileLauncher    { _ -> }

    val infiniteTransition = rememberInfiniteTransition(label = "ambient")
    val ambientAlpha by infiniteTransition.animateFloat(
        initialValue = 0.05f, targetValue = 0.13f,
        animationSpec = infiniteRepeatable(tween(4000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "ambient"
    )
    val scanPulse by infiniteTransition.animateFloat(
        initialValue = 0.7f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "scan_pulse"
    )

    Box(modifier = Modifier.fillMaxSize().background(c.background)) {
        // Ambient blobs
        Box(
            Modifier.size(350.dp).offset(x = (-100).dp, y = 80.dp).blur(140.dp)
                .background(c.ambientBlob1.copy(alpha = ambientAlpha), CircleShape)
        )
        Box(
            Modifier.size(250.dp).align(Alignment.BottomCenter).offset(y = 60.dp).blur(120.dp)
                .background(c.ambientBlob2.copy(alpha = ambientAlpha * 1.4f), CircleShape)
        )

        Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {

            HomeTopBar(
                isDarkTheme    = isDarkTheme,
                onToggleTheme  = onToggleTheme
            )

            HomeHero()

            Spacer(Modifier.height(28.dp))

            UploadCard(
                modifier = Modifier.padding(horizontal = 20.dp),
                onClick  = { openFiles() }
            )

            Spacer(Modifier.height(20.dp))

            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            ) {
                ScanCard(
                    modifier   = Modifier.fillMaxWidth().padding(top = 14.dp),
                    pulseScale = scanPulse,
                    onClick    = { openCamera() }
                )

                Box(
                    modifier = Modifier.align(Alignment.TopCenter)
                        .clip(RoundedCornerShape(20.dp))
                        .background(c.goldShine)
                        .padding(horizontal = 16.dp, vertical = 5.dp)
                ) {
                    Text(
                        "QUICK SCAN",
                        color = Color(0xFF1A0F00),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            HomeBottomNav(
                selected  = "scan",
                onHistory = onNavigateToHistory,
                onFiles   = onNavigateToFiles
            )
        }
    }
}

// ─── Top Bar ──────────────────────────────────────────────────────────────────
// Burger menu eliminat. Butonul ⚙ face toggle theme.
@Composable
private fun HomeTopBar(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val c = appColors
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ── STÂNGA: gol (burger eliminat) ─────────────────────────────────────
        Spacer(Modifier.size(36.dp))

        Spacer(Modifier.weight(1f))

        // ── CENTRU: titlu ─────────────────────────────────────────────────────
        Text(
            "Scanner",
            style = TextStyle(
                brush = Brush.linearGradient(listOf(c.purpleGlow, c.purpleNeon)),
                fontSize = 18.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp
            )
        )

        Spacer(Modifier.weight(1f))

        // ── DREAPTA: toggle theme ─────────────────────────────────────────────
        Box(
            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                .background(c.glassWhite)
                .drawBehind {
                    drawRoundRect(c.glassBorder, cornerRadius = CornerRadius(10.dp.toPx()), style = Stroke(1f))
                }
                .clickable(remember { MutableInteractionSource() }, null) { onToggleTheme() },
            contentAlignment = Alignment.Center
        ) {
            // Iconă soare/lună în funcție de temă
            Text(
                if (isDarkTheme) "☀" else "🌙",
                fontSize = 16.sp,
                color = c.purpleNeon
            )
        }
    }
}

// ─── Hero ─────────────────────────────────────────────────────────────────────
@Composable
private fun HomeHero() {
    val c = appColors
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(80); visible = true }

    AnimatedVisibility(
        visible,
        enter = fadeIn(tween(700)) + slideInVertically(tween(700, easing = EaseOutQuart)) { -30 }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "skip unnecessary\nsharing with us",
                style = TextStyle(
                    brush = Brush.linearGradient(listOf(c.silverText, c.heroGradientEnd)),
                    fontSize = 28.sp, fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic, lineHeight = 36.sp
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(width = 16.dp, height = 1.dp)
                        .background(Brush.horizontalGradient(listOf(Color.Transparent, c.goldShine)))
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "end to end encryption",
                    color = c.goldShine, fontSize = 12.sp,
                    fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier.size(width = 16.dp, height = 1.dp)
                        .background(Brush.horizontalGradient(listOf(c.goldShine, Color.Transparent)))
                )
            }
        }
    }
}

// ─── Upload Card ──────────────────────────────────────────────────────────────
@Composable
private fun UploadCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val c = appColors
    var hovered by remember { mutableStateOf(false) }
    val borderAlpha by animateFloatAsState(if (hovered) 0.6f else 0.3f, tween(200), label = "border")

    Box(
        modifier = modifier.fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(c.surface)
            .drawBehind {
                val path = androidx.compose.ui.graphics.Path().apply {
                    addRoundRect(
                        androidx.compose.ui.geometry.RoundRect(
                            left = 1f, top = 1f,
                            right = size.width - 1f, bottom = size.height - 1f,
                            cornerRadius = CornerRadius(16.dp.toPx())
                        )
                    )
                }
                drawPath(
                    path = path,
                    color = c.purpleGlow.copy(alpha = borderAlpha),
                    style = Stroke(
                        width = 1.5f,
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                            floatArrayOf(10f, 8f), 0f
                        )
                    )
                )
            }
            .clickable(remember { MutableInteractionSource() }, null) {
                hovered = true
                onClick()
            }
            .padding(vertical = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(50.dp).clip(CircleShape)
                    .background(c.uploadIconBg)
                    .drawBehind {
                        drawCircle(c.purpleGlow.copy(alpha = 0.3f), style = Stroke(1f))
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("↑", fontSize = 22.sp, color = c.purpleNeon, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(14.dp))
            Text(
                "UPLOAD A FILE",
                color = c.silverText, fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp
            )
            Spacer(Modifier.height(4.dp))
            Text("Drag and drop or browse gallery", color = c.dimText, fontSize = 13.sp)
        }
    }
}

// ─── Scan Card ────────────────────────────────────────────────────────────────
@Composable
private fun ScanCard(modifier: Modifier = Modifier, pulseScale: Float, onClick: () -> Unit) {
    val c = appColors
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.verticalGradient(listOf(c.scanGradStart, c.scanGradEnd)))
            .drawBehind {
                drawRoundRect(
                    color = c.scanGlowBorder.copy(alpha = 0.25f),
                    cornerRadius = CornerRadius(20.dp.toPx()),
                    style = Stroke(1.5f)
                )
            }
            .clickable(remember { MutableInteractionSource() }, null) { onClick() }
            .padding(vertical = 36.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(64.dp), contentAlignment = Alignment.Center) {
                ScanFrame(alpha = pulseScale)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "SCAN", color = Color.White, fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold, letterSpacing = 4.sp
            )
        }
    }
}

// ─── Scan Frame ───────────────────────────────────────────────────────────────
@Composable
private fun ScanFrame(alpha: Float) {
    val color  = Color.White.copy(alpha = alpha)
    val stroke = 3.dp
    val corner = 14.dp
    val size   = 56.dp

    Box(modifier = Modifier.size(size)) {
        Box(Modifier.align(Alignment.TopStart)) {
            Box(Modifier.size(width = corner, height = stroke).background(color, RoundedCornerShape(stroke)))
            Box(Modifier.size(width = stroke, height = corner).background(color, RoundedCornerShape(stroke)))
        }
        Box(Modifier.align(Alignment.TopEnd)) {
            Box(Modifier.size(width = corner, height = stroke).align(Alignment.TopEnd).background(color, RoundedCornerShape(stroke)))
            Box(Modifier.size(width = stroke, height = corner).align(Alignment.TopEnd).background(color, RoundedCornerShape(stroke)))
        }
        Box(Modifier.align(Alignment.BottomStart)) {
            Box(Modifier.size(width = corner, height = stroke).align(Alignment.BottomStart).background(color, RoundedCornerShape(stroke)))
            Box(Modifier.size(width = stroke, height = corner).align(Alignment.BottomStart).background(color, RoundedCornerShape(stroke)))
        }
        Box(Modifier.align(Alignment.BottomEnd)) {
            Box(Modifier.size(width = corner, height = stroke).align(Alignment.BottomEnd).background(color, RoundedCornerShape(stroke)))
            Box(Modifier.size(width = stroke, height = corner).align(Alignment.BottomEnd).background(color, RoundedCornerShape(stroke)))
        }
        Box(
            modifier = Modifier.size(16.dp).align(Alignment.Center).clip(CircleShape)
                .border(2.dp, color, CircleShape)
        )
    }
}

// ─── Bottom Nav ───────────────────────────────────────────────────────────────
@Composable
private fun HomeBottomNav(
    selected: String,
    onHistory: () -> Unit,
    onFiles: () -> Unit
) {
    val c = appColors
    data class NavItem(val id: String, val icon: String, val label: String, val onClick: () -> Unit)

    val items = listOf(
        NavItem("history", "◷", "HISTORY", onHistory),
        NavItem("scan",    "⊙", "SCAN",    {}),
        NavItem("files",   "⊟", "FILES",   onFiles),
    )

    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(c.navBarBg)
            .drawBehind {
                drawLine(c.navBorderLine, Offset(0f, 0f), Offset(size.width, 0f), 1f)
            }
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEach { item ->
            val isSelected = item.id == selected
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 20.dp)
                    .clickable(remember { MutableInteractionSource() }, null) { item.onClick() }
            ) {
                if (isSelected) {
                    Box(
                        Modifier.size(width = 24.dp, height = 2.dp).background(
                            Brush.horizontalGradient(listOf(Color.Transparent, c.purpleGlow, Color.Transparent)),
                            RoundedCornerShape(1.dp)
                        )
                    )
                    Spacer(Modifier.height(4.dp))
                } else {
                    Spacer(Modifier.height(6.dp))
                }
                Text(item.icon, fontSize = 22.sp, color = if (isSelected) c.purpleGlow else c.dimText)
                Spacer(Modifier.height(3.dp))
                Text(
                    item.label, fontSize = 9.sp, letterSpacing = 1.2.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) c.purpleNeon else c.dimText
                )
            }
        }
    }
}
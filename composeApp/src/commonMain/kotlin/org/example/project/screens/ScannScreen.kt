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

// ─── Culori (identice cu HistoryScreen) ──────────────────────────────────────
private val Void         = Color(0xFF060608)
private val NightSurface = Color(0xFF12121C)
private val GlassWhite   = Color(0x0FFFFFFF)
private val GlassBorder  = Color(0x18FFFFFF)
private val PurpleCore   = Color(0xFF7C3AED)
private val PurpleGlow   = Color(0xFF9F67FF)
private val PurpleNeon   = Color(0xFFBF9FFF)
private val PurpleDim    = Color(0xFF2D1B5E)
private val PurpleBright = Color(0xFFAB47FF)
private val GoldShine    = Color(0xFFFFBD2E)
private val SilverText   = Color(0xFFE2E2F0)
private val DimText      = Color(0xFF6B6B8A)
private val GhostText    = Color(0xFF35354A)


// ─── HomeScreen ───────────────────────────────────────────────────────────────
@Composable
fun HomeScreen(
    onNavigateToHistory: () -> Unit = {},
    onNavigateToFiles: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    // 1. Am eliminat showUploadSheet pentru că nu mai avem nevoie de popup
    var showCameraActive by remember { mutableStateOf(false) }
    var uploadedCount by remember { mutableStateOf(3) }

    val openCamera  = rememberCameraLauncher  { bytes -> /* salvează */ }
    val openGallery = rememberGalleryLauncher { bytes -> /* salvează */ }
    // Acesta este launcher-ul care deschide direct folderele/managerul de fișiere
    val openFiles   = rememberFileLauncher    { bytes -> /* salvează */ }

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

    Box(modifier = Modifier.fillMaxSize().background(Void)) {
        // Ambient blobs
        Box(Modifier.size(350.dp).offset(x = (-100).dp, y = 80.dp).blur(140.dp)
            .background(PurpleCore.copy(alpha = ambientAlpha), CircleShape))
        Box(Modifier.size(250.dp).align(Alignment.BottomCenter).offset(y = 60.dp).blur(120.dp)
            .background(Color(0xFF3D0A8A).copy(alpha = ambientAlpha * 1.4f), CircleShape))

        Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {

            // ── Top Bar ───────────────────────────────────────────────────────
            HomeTopBar(onSettingsClick = onSettingsClick)

            // ── Hero Text ─────────────────────────────────────────────────────
            HomeHero()

            Spacer(Modifier.height(28.dp))

            // ── Upload Card ───────────────────────────────────────────────────
            UploadCard(
                modifier = Modifier.padding(horizontal = 20.dp),
                onClick = {
                    // 2. MODIFICARE: Apelăm direct openFiles() în loc să deschidem sheet-ul
                    openFiles()
                }
            )

            Spacer(Modifier.height(20.dp))

            // ── Quick Scan badge + Scan Button ────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                // Scan card mare purple
                ScanCard(
                    modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                    pulseScale = scanPulse,
                    onClick = { 
                        openCamera()
                    }
                )

                // Badge "QUICK SCAN" centrat sus
                Box(
                    modifier = Modifier.align(Alignment.TopCenter)
                        .clip(RoundedCornerShape(20.dp))
                        .background(GoldShine)
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

            // ── Bottom Nav ────────────────────────────────────────────────────
            HomeBottomNav(
                selected = "scan",
                onHistory = onNavigateToHistory,
                onFiles = onNavigateToFiles
            )
        }
    }
}

// ─── Top Bar ──────────────────────────────────────────────────────────────────
@Composable
private fun HomeTopBar(onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Box(Modifier.size(width = 22.dp, height = 2.dp).background(PurpleGlow, RoundedCornerShape(1.dp)))
            Box(Modifier.size(width = 15.dp, height = 2.dp).background(PurpleNeon.copy(alpha = 0.5f), RoundedCornerShape(1.dp)))
        }
        Spacer(Modifier.weight(1f))
        Text(
            "Scanner",
            style = TextStyle(
                brush = Brush.linearGradient(listOf(PurpleGlow, PurpleNeon)),
                fontSize = 18.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp
            )
        )
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                .background(GlassWhite)
                .drawBehind { drawRoundRect(GlassBorder, cornerRadius = CornerRadius(10.dp.toPx()), style = Stroke(1f)) }
                .clickable(remember { MutableInteractionSource() }, null) { onSettingsClick() },
            contentAlignment = Alignment.Center
        ) { Text("⚙", fontSize = 16.sp, color = PurpleNeon) }
    }
}

// ─── Hero ─────────────────────────────────────────────────────────────────────
@Composable
private fun HomeHero() {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(80); visible = true }

    AnimatedVisibility(visible, enter = fadeIn(tween(700)) + slideInVertically(tween(700, easing = EaseOutQuart)) { -30 }) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "skip unnecessary\nsharing with us",
                style = TextStyle(
                    brush = Brush.linearGradient(listOf(Color.White, Color(0xFFCCBBFF))),
                    fontSize = 28.sp, fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic, lineHeight = 36.sp
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(width = 16.dp, height = 1.dp)
                    .background(Brush.horizontalGradient(listOf(Color.Transparent, GoldShine))))
                Spacer(Modifier.width(8.dp))
                Text(
                    "end to end encryption",
                    color = GoldShine, fontSize = 12.sp,
                    fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp
                )
                Spacer(Modifier.width(8.dp))
                Box(Modifier.size(width = 16.dp, height = 1.dp)
                    .background(Brush.horizontalGradient(listOf(GoldShine, Color.Transparent))))
            }
        }
    }
}

// ─── Upload Card ──────────────────────────────────────────────────────────────
@Composable
private fun UploadCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    var hovered by remember { mutableStateOf(false) }
    val borderAlpha by animateFloatAsState(if (hovered) 0.6f else 0.3f, tween(200), label = "border")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(NightSurface)
            .drawBehind {
                // Dashed border purple — simulate con trattini
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
                    color = PurpleGlow.copy(alpha = borderAlpha),
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
            // Icona document
            Box(
                modifier = Modifier.size(50.dp).clip(CircleShape)
                    .background(PurpleDim)
                    .drawBehind {
                        drawCircle(PurpleGlow.copy(alpha = 0.3f), style = Stroke(1f))
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("↑", fontSize = 22.sp, color = PurpleNeon, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(14.dp))
            Text(
                "UPLOAD A FILE",
                color = SilverText, fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Drag and drop or browse gallery",
                color = DimText, fontSize = 13.sp
            )
        }
    }
}

// ─── Scan Card ────────────────────────────────────────────────────────────────
@Composable
private fun ScanCard(modifier: Modifier = Modifier, pulseScale: Float, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(listOf(Color(0xFF9B30FF), Color(0xFF6B21D6)))
            )
            .drawBehind {
                // Glow sopra
                drawRoundRect(
                    color = Color(0xFFBF7FFF).copy(alpha = 0.25f),
                    cornerRadius = CornerRadius(20.dp.toPx()),
                    style = Stroke(1.5f)
                )
            }
            .clickable(remember { MutableInteractionSource() }, null) { onClick() }
            .padding(vertical = 36.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Scan frame animat
            Box(
                modifier = Modifier.size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                // Corners del frame scanner
                ScanFrame(alpha = pulseScale)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "SCAN",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp
            )
        }
    }
}

// ─── Scan Frame Icon (4 angoli) ───────────────────────────────────────────────
@Composable
private fun ScanFrame(alpha: Float) {
    val color = Color.White.copy(alpha = alpha)
    val stroke = 3.dp
    val corner = 14.dp
    val size = 56.dp

    Box(modifier = Modifier.size(size)) {
        // Top-left
        Box(Modifier.align(Alignment.TopStart)) {
            Box(Modifier.size(width = corner, height = stroke).background(color, RoundedCornerShape(stroke)))
            Box(Modifier.size(width = stroke, height = corner).background(color, RoundedCornerShape(stroke)))
        }
        // Top-right
        Box(Modifier.align(Alignment.TopEnd)) {
            Box(Modifier.size(width = corner, height = stroke).align(Alignment.TopEnd).background(color, RoundedCornerShape(stroke)))
            Box(Modifier.size(width = stroke, height = corner).align(Alignment.TopEnd).background(color, RoundedCornerShape(stroke)))
        }
        // Bottom-left
        Box(Modifier.align(Alignment.BottomStart)) {
            Box(Modifier.size(width = corner, height = stroke).align(Alignment.BottomStart).background(color, RoundedCornerShape(stroke)))
            Box(Modifier.size(width = stroke, height = corner).align(Alignment.BottomStart).background(color, RoundedCornerShape(stroke)))
        }
        // Bottom-right
        Box(Modifier.align(Alignment.BottomEnd)) {
            Box(Modifier.size(width = corner, height = stroke).align(Alignment.BottomEnd).background(color, RoundedCornerShape(stroke)))
            Box(Modifier.size(width = stroke, height = corner).align(Alignment.BottomEnd).background(color, RoundedCornerShape(stroke)))
        }
        // Cerchio centrale
        Box(
            modifier = Modifier.size(16.dp).align(Alignment.Center).clip(CircleShape)
                .border(2.dp, color, CircleShape)
        )
    }
}

// ─── Upload Bottom Sheet ──────────────────────────────────────────────────────
@Composable
private fun UploadSheet(
    onDismiss: () -> Unit,
    onGallery: () -> Unit,
    onFiles: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(NightSurface)
            .drawBehind {
                drawLine(
                    color = GlassBorder,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1f
                )
            }
            .padding(bottom = 32.dp)
            .clickable(remember { MutableInteractionSource() }, null) { /* consume click */ }
    ) {
        // Handle
        Box(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(Modifier.size(width = 40.dp, height = 4.dp)
                .background(GhostText, RoundedCornerShape(2.dp)))
        }

        Spacer(Modifier.height(20.dp))

        Text(
            "Alege sursa",
            color = SilverText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Text(
            "De unde vrei să încarci documentul?",
            color = DimText,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
        )

        Spacer(Modifier.height(20.dp))

        // Galerie
        SheetOption(
            icon = "🖼",
            title = "Galerie foto",
            subtitle = "Alege o poză din galeria ta",
            accentColor = Color(0xFF10B981),
            onClick = onGallery
        )

        Spacer(Modifier.height(10.dp))

        // Fișiere
        SheetOption(
            icon = "📁",
            title = "Fișiere",
            subtitle = "PDF, JPG, PNG din storage",
            accentColor = PurpleGlow,
            onClick = onFiles
        )

        Spacer(Modifier.height(16.dp))

        // Cancel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(GlassWhite)
                .drawBehind { drawRoundRect(GlassBorder, cornerRadius = CornerRadius(14.dp.toPx()), style = Stroke(1f)) }
                .clickable(remember { MutableInteractionSource() }, null) { onDismiss() }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Anulează", color = DimText, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun SheetOption(
    icon: String,
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1C1C28))
            .drawBehind {
                drawRoundRect(accentColor.copy(alpha = 0.2f), cornerRadius = CornerRadius(16.dp.toPx()), style = Stroke(1f))
                drawLine(
                    color = accentColor.copy(alpha = 0.7f),
                    start = Offset(0f, 20.dp.toPx()),
                    end = Offset(0f, size.height - 20.dp.toPx()),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            .clickable(remember { MutableInteractionSource() }, null) { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(46.dp).clip(RoundedCornerShape(12.dp))
                .background(accentColor.copy(alpha = 0.12f))
                .drawBehind { drawRoundRect(accentColor.copy(alpha = 0.25f), cornerRadius = CornerRadius(12.dp.toPx()), style = Stroke(1f)) },
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 22.sp)
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, color = SilverText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = DimText, fontSize = 12.sp)
        }
        Spacer(Modifier.weight(1f))
        Text("→", color = accentColor, fontSize = 18.sp)
    }
}

// ─── Camera Overlay (placeholder — actual pe fiecare platformă) ───────────────
@Composable
private fun CameraOverlay(onClose: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "scan_line")
    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "scan_y"
    )

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.Black.copy(alpha = 0.92f))
    ) {
        // Overlay semi-transparent cu zona de scan
        Column(
            modifier = Modifier.fillMaxSize().systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp))
            Text(
                "Poziționează documentul",
                color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(6.dp))
            Text("în cadrul de mai jos", color = DimText, fontSize = 13.sp)

            Spacer(Modifier.height(40.dp))

            // Scanner frame mare
            Box(
                modifier = Modifier.size(280.dp)
                    .drawBehind {
                        val c = PurpleGlow
                        val sw = 3.dp.toPx()
                        val cr = 20.dp.toPx()
                        val seg = 50.dp.toPx()
                        val w = size.width; val h = size.height

                        // 4 corners
                        // TL
                        drawLine(c, Offset(0f, cr), Offset(0f, cr + seg), sw, StrokeCap.Round)
                        drawLine(c, Offset(cr, 0f), Offset(cr + seg, 0f), sw, StrokeCap.Round)
                        // TR
                        drawLine(c, Offset(w, cr), Offset(w, cr + seg), sw, StrokeCap.Round)
                        drawLine(c, Offset(w - cr, 0f), Offset(w - cr - seg, 0f), sw, StrokeCap.Round)
                        // BL
                        drawLine(c, Offset(0f, h - cr), Offset(0f, h - cr - seg), sw, StrokeCap.Round)
                        drawLine(c, Offset(cr, h), Offset(cr + seg, h), sw, StrokeCap.Round)
                        // BR
                        drawLine(c, Offset(w, h - cr), Offset(w, h - cr - seg), sw, StrokeCap.Round)
                        drawLine(c, Offset(w - cr, h), Offset(w - cr - seg, h), sw, StrokeCap.Round)

                        // Scan line animata
                        val lineY = h * scanLineY
                        drawLine(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, PurpleGlow, Color(0xFFFFBD2E), PurpleGlow, Color.Transparent)
                            ),
                            start = Offset(10f, lineY),
                            end = Offset(w - 10f, lineY),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
            )

            Spacer(Modifier.weight(1f))

            // Pulsing capture button
            val capturePulse by infiniteTransition.animateFloat(
                initialValue = 0.85f, targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(900, easing = EaseInOutSine), RepeatMode.Reverse),
                label = "capture"
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp, vertical = 40.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Annulla
                Box(
                    modifier = Modifier.size(56.dp).clip(CircleShape)
                        .background(GlassWhite)
                        .drawBehind { drawCircle(GlassBorder, style = Stroke(1f)) }
                        .clickable(remember { MutableInteractionSource() }, null) { onClose() },
                    contentAlignment = Alignment.Center
                ) { Text("✕", color = DimText, fontSize = 18.sp) }

                // Capture
                Box(
                    modifier = Modifier
                        .size((72 * capturePulse).dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(listOf(PurpleBright, PurpleCore))
                        )
                        .drawBehind { drawCircle(PurpleNeon.copy(alpha = 0.4f * capturePulse), radius = size.minDimension / 2 + 6.dp.toPx(), style = Stroke(2f)) }
                        .clickable(remember { MutableInteractionSource() }, null) { onClose() },
                    contentAlignment = Alignment.Center
                ) { Text("📷", fontSize = 26.sp) }

                // Flash placeholder
                Box(
                    modifier = Modifier.size(56.dp).clip(CircleShape)
                        .background(GlassWhite)
                        .drawBehind { drawCircle(GlassBorder, style = Stroke(1f)) },
                    contentAlignment = Alignment.Center
                ) { Text("⚡", fontSize = 18.sp) }
            }
        }
    }
}

// ─── Bottom Nav con navigazione ───────────────────────────────────────────────
@Composable
private fun HomeBottomNav(
    selected: String,
    onHistory: () -> Unit,
    onFiles: () -> Unit
) {
    data class NavItem(val id: String, val icon: String, val label: String, val onClick: () -> Unit)

    val items = listOf(
        NavItem("history", "◷", "HISTORY", onHistory),
        NavItem("scan",    "⊙", "SCAN",    {}),
        NavItem("files",   "⊟", "FILES",   onFiles),
    )

    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(NightSurface)
            .drawBehind {
                drawLine(GlassBorder, Offset(0f, 0f), Offset(size.width, 0f), 1f)
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
                    Box(Modifier.size(width = 24.dp, height = 2.dp).background(
                        Brush.horizontalGradient(listOf(Color.Transparent, PurpleGlow, Color.Transparent)),
                        RoundedCornerShape(1.dp)
                    ))
                    Spacer(Modifier.height(4.dp))
                } else {
                    Spacer(Modifier.height(6.dp))
                }
                Text(item.icon, fontSize = 22.sp, color = if (isSelected) PurpleGlow else DimText)
                Spacer(Modifier.height(3.dp))
                Text(
                    item.label, fontSize = 9.sp, letterSpacing = 1.2.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) PurpleNeon else DimText
                )
            }
        }
    }
}
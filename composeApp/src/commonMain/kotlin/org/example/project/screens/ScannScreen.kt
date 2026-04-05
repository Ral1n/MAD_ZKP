package org.example.project.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.project.ScannerEngine
import org.example.project.dropFileTarget
import org.example.project.isDesktop
import org.example.project.theme.appColors
import org.example.project.theme.appStrings

// ─── HomeScreen ───────────────────────────────────────────────────────────────
@Composable
fun HomeScreen(
    onNavigateToHistory: () -> Unit = {},
    onNavigateToFiles: () -> Unit = {},
    isDarkTheme: Boolean = true,
    onToggleTheme: () -> Unit = {},
    tessDataPath: String = "",
    onIdentityCardDetected: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val c = appColors
    val scope = rememberCoroutineScope()
    var isScanning by remember { mutableStateOf(false) }
    var showIdentityDialog by remember { mutableStateOf(false) }

    val idKeywords = listOf("IDENTITY CARD", "CARTE DE IDENTITATE", "IDENTITATE", "IDENTITY", "CARTE")

    fun processBytes(bytes: ByteArray?) {
        if (bytes != null && (tessDataPath.isNotEmpty() || isDesktop)) {
            isScanning = true
            scope.launch {
                val text = ScannerEngine(tessDataPath).scanImage(bytes)
                println("OCR_RESULT: $text")
                isScanning = false
                val upper = text.uppercase()
                if (idKeywords.any { upper.contains(it) }) {
                    onIdentityCardDetected()
                    showIdentityDialog = true
                }
            }
        }
    }

    val openCamera  = rememberCameraLauncher { bytes -> processBytes(bytes) }
    val openGallery = rememberGalleryLauncher { bytes -> processBytes(bytes) }
    val openFiles   = rememberFileLauncher    { bytes -> processBytes(bytes) }

    val infiniteTransition = rememberInfiniteTransition(label = "ambient")

    val ambientAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.05f,
        targetValue   = 0.13f,
        animationSpec = infiniteRepeatable(tween(4000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label         = "ambient"
    )

    val scanPulse by if (!isDesktop) {
        infiniteTransition.animateFloat(
            initialValue  = 0.7f,
            targetValue   = 1f,
            animationSpec = infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
            label         = "scan_pulse"
        )
    } else {
        infiniteTransition.animateFloat(
            initialValue  = 1f,
            targetValue   = 1f,
            animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Restart),
            label         = "scan_pulse_desktop"
        )
    }

    val orbitRotation by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 360f,
        animationSpec = infiniteRepeatable(tween(18000, easing = LinearEasing), RepeatMode.Restart),
        label         = "orbit_rotation"
    )

    val orbitRotationSlow by infiniteTransition.animateFloat(
        initialValue  = 180f,
        targetValue   = 540f,
        animationSpec = infiniteRepeatable(tween(24000, easing = LinearEasing), RepeatMode.Restart),
        label         = "orbit_rotation_slow"
    )

    Box(modifier = Modifier.fillMaxSize().background(c.background)) {
        Box(
            Modifier.size(350.dp).offset(x = (-100).dp, y = 80.dp).blur(140.dp)
                .background(c.ambientBlob1.copy(alpha = ambientAlpha), CircleShape)
        )
        Box(
            Modifier.size(250.dp).align(Alignment.BottomCenter).offset(y = 60.dp).blur(120.dp)
                .background(c.ambientBlob2.copy(alpha = ambientAlpha * 1.4f), CircleShape)
        )

        Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
            HomeTopBar(isDarkTheme = isDarkTheme, onToggleTheme = onToggleTheme, onProfile = onNavigateToProfile)

            if (isDesktop) {
                DesktopHomeCenter(
                    modifier          = Modifier.weight(1f).fillMaxWidth(),
                    onUploadClick     = { openFiles() },
                    onFileDrop        = { bytes -> processBytes(bytes) },
                    orbitRotation     = orbitRotation,
                    orbitRotationSlow = orbitRotationSlow
                )
            } else {
                HomeHero()
                Spacer(Modifier.height(28.dp))
                UploadCard(modifier = Modifier.padding(horizontal = 20.dp), onClick = { openFiles() })
                Spacer(Modifier.height(20.dp))
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                    ScanCard(
                        modifier   = Modifier.fillMaxWidth().padding(top = 14.dp),
                        pulseScale = scanPulse,
                        onClick    = { openCamera() }
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .clip(RoundedCornerShape(20.dp))
                            .background(c.goldShine)
                            .padding(horizontal = 16.dp, vertical = 5.dp)
                    ) {
                        Text(
                            appStrings.quickScan,
                            color         = Color(0xFF1A0F00),
                            fontSize      = 11.sp,
                            fontWeight    = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
            }

            HomeBottomNav(selected = "scan", onHistory = onNavigateToHistory, onFiles = onNavigateToFiles)
        }

        if (isScanning) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color       = c.purpleNeon,
                    strokeWidth = 4.dp,
                    modifier    = Modifier.size(64.dp)
                )
            }
        }
    }

    if (showIdentityDialog) {
        IdentityCardVerifiedDialog(onDismiss = { showIdentityDialog = false })
    }
}

// ─── Identity Card Verified Dialog ───────────────────────────────────────────
@Composable
private fun IdentityCardVerifiedDialog(onDismiss: () -> Unit) {
    val c = appColors
    val s = appStrings
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(c.surface)
                .drawBehind {
                    drawRoundRect(
                        color        = c.emeraldGlow.copy(alpha = 0.35f),
                        cornerRadius = CornerRadius(24.dp.toPx()),
                        style        = Stroke(1.5f)
                    )
                }
                .padding(28.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(c.emeraldGlow.copy(alpha = 0.12f))
                        .drawBehind { drawCircle(c.emeraldGlow.copy(alpha = 0.35f), style = Stroke(1.5f)) },
                    contentAlignment = Alignment.Center
                ) {
                    Text("✓", fontSize = 28.sp, color = c.emeraldGlow, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(20.dp))
                Text(
                    s.idVerifiedTitle,
                    color      = c.silverText,
                    fontSize   = 17.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign  = TextAlign.Center
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    s.idVerifiedSubtitle,
                    color     = c.dimText,
                    fontSize  = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 19.sp
                )
                Spacer(Modifier.height(28.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.horizontalGradient(listOf(c.purpleCore, c.purpleBright.copy(alpha = 0.9f))))
                        .clickable(remember { MutableInteractionSource() }, null) { onDismiss() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(s.gotIt, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ─── Top Bar ──────────────────────────────────────────────────────────────────
@Composable
private fun HomeTopBar(isDarkTheme: Boolean, onToggleTheme: () -> Unit, onProfile: () -> Unit) {
    val c = appColors
    Row(
        modifier          = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile icon — left corner
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(listOf(c.purpleCore.copy(alpha = 0.7f), c.purpleDim))
                )
                .drawBehind {
                    drawCircle(
                        brush  = Brush.sweepGradient(listOf(c.purpleGlow.copy(alpha = 0.6f), c.goldShine.copy(alpha = 0.3f), c.purpleGlow.copy(alpha = 0.6f))),
                        radius = size.minDimension / 2 - 1.dp.toPx(),
                        style  = Stroke(1.5f)
                    )
                }
                .clickable(remember { MutableInteractionSource() }, null) { onProfile() },
            contentAlignment = Alignment.Center
        ) {
            Text("A", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.weight(1f))
        Text(
            appStrings.scannerTitle,
            style = TextStyle(
                brush         = Brush.linearGradient(listOf(c.purpleGlow, c.purpleNeon)),
                fontSize      = 18.sp,
                fontWeight    = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )
        Spacer(Modifier.weight(1f))
        // Theme toggle — right corner
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(c.glassWhite)
                .drawBehind {
                    drawRoundRect(c.glassBorder, cornerRadius = CornerRadius(10.dp.toPx()), style = Stroke(1f))
                }
                .clickable(remember { MutableInteractionSource() }, null) { onToggleTheme() },
            contentAlignment = Alignment.Center
        ) {
            Text(if (isDarkTheme) "☀" else "🌙", fontSize = 16.sp, color = c.purpleNeon)
        }
    }
}

// ─── Hero ─────────────────────────────────────────────────────────────────────
@Composable
private fun HomeHero() {
    val c = appColors
    val s = appStrings
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(80); visible = true }

    AnimatedVisibility(
        visible,
        enter = fadeIn(tween(700)) + slideInVertically(tween(700, easing = LinearOutSlowInEasing)) { -30 }
    ) {
        Column(
            modifier            = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text      = s.heroText,
                style     = TextStyle(
                    brush      = Brush.linearGradient(listOf(c.silverText, c.heroGradientEnd)),
                    fontSize   = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle  = FontStyle.Italic,
                    lineHeight = 36.sp
                ),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(width = 16.dp, height = 1.dp)
                        .background(Brush.horizontalGradient(listOf(Color.Transparent, c.goldShine)))
                )
                Spacer(Modifier.width(8.dp))
                Text(s.heroSubtitle, color = c.goldShine, fontSize = 12.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp)
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier.size(width = 16.dp, height = 1.dp)
                        .background(Brush.horizontalGradient(listOf(c.goldShine, Color.Transparent)))
                )
            }
        }
    }
}

// ─── Desktop Center ───────────────────────────────────────────────────────────
@Composable
private fun DesktopHomeCenter(
    modifier: Modifier = Modifier,
    onUploadClick: () -> Unit,
    onFileDrop: (ByteArray) -> Unit,
    orbitRotation: Float,
    orbitRotationSlow: Float
) {
    val c = appColors
    var isDragOver by remember { mutableStateOf(false) }
    val dragBorderAlpha by animateFloatAsState(
        targetValue   = if (isDragOver) 0.9f else 0f,
        animationSpec = tween(200),
        label         = "drag_border"
    )

    BoxWithConstraints(
        modifier.fillMaxSize()
            .dropFileTarget(
                onDrop        = onFileDrop,
                onHoverChange = { isDragOver = it }
            )
    ) {
        // REGULA: maxWidth se foloseste EXCLUSIV in interiorul acestui bloc BoxWithConstraints.
        // Valorile calculate sunt pasate mai departe ca parametri simpli (Dp, Float, Boolean).
        val shortest          = if (maxWidth < maxHeight) maxWidth else maxHeight
        val outerDisc         = shortest * 0.82f
        val midRing           = shortest * 0.68f
        val innerRing         = shortest * 0.54f
        val horizontalPad: Dp = when {
            maxWidth < 360.dp -> 12.dp
            maxWidth < 520.dp -> 18.dp
            else              -> 28.dp
        }
        val uploadWidthFraction = when {
            maxWidth < 380.dp -> 1f
            maxWidth < 640.dp -> 0.96f
            else              -> 0.88f
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.size(outerDisc).align(Alignment.Center).clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                c.purpleGlow.copy(alpha = 0.14f),
                                c.purpleDim.copy(alpha = if (c.isDark) 0.28f else 0.50f),
                                Color.Transparent
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier.size(outerDisc).align(Alignment.Center).drawBehind {
                    val r = size.minDimension / 2f
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(c.purpleGlow.copy(alpha = 0.55f), c.goldShine.copy(alpha = 0.38f),
                                c.purpleNeon.copy(alpha = 0.48f), c.purpleGlow.copy(alpha = 0.55f)),
                            center = center
                        ),
                        radius = r - 1.5.dp.toPx(),
                        style  = Stroke(2.dp.toPx())
                    )
                }
            )
            Box(
                modifier = Modifier.size(outerDisc).align(Alignment.Center).rotate(orbitRotation).drawBehind {
                    drawCircle(color = c.goldShine, radius = 4.dp.toPx(), center = Offset(size.width / 2f, 4.dp.toPx()), alpha = 0.9f)
                    drawCircle(color = c.goldShine.copy(alpha = 0.25f), radius = 9.dp.toPx(), center = Offset(size.width / 2f, 4.dp.toPx()))
                }
            )
            Box(
                modifier = Modifier.size(outerDisc).align(Alignment.Center).rotate(orbitRotationSlow).drawBehind {
                    drawCircle(color = c.purpleNeon, radius = 3.dp.toPx(), center = Offset(size.width / 2f, size.height - 3.dp.toPx()), alpha = 0.7f)
                    drawCircle(color = c.purpleNeon.copy(alpha = 0.2f), radius = 7.dp.toPx(), center = Offset(size.width / 2f, size.height - 3.dp.toPx()))
                }
            )
            Box(
                modifier = Modifier.size(midRing).align(Alignment.Center).drawBehind {
                    drawCircle(color = c.goldShine.copy(alpha = 0.15f), style = Stroke(1.dp.toPx()))
                }
            )
            Box(
                modifier = Modifier.size(innerRing).align(Alignment.Center).drawBehind {
                    drawCircle(color = c.purpleGlow.copy(alpha = 0.10f), style = Stroke(0.5.dp.toPx()))
                }
            )
            Column(
                modifier            = Modifier.align(Alignment.Center).fillMaxWidth().padding(horizontal = horizontalPad),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HomeHero()
                Spacer(Modifier.height(32.dp))
                UploadCard(
                    modifier = Modifier.widthIn(min = 240.dp, max = 620.dp).fillMaxWidth(uploadWidthFraction),
                    onClick  = onUploadClick
                )
            }

            // Drag-over overlay
            if (dragBorderAlpha > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(c.purpleGlow.copy(alpha = 0.07f * dragBorderAlpha))
                        .drawBehind {
                            drawRoundRect(
                                color        = c.purpleNeon.copy(alpha = 0.7f * dragBorderAlpha),
                                cornerRadius = CornerRadius(24.dp.toPx()),
                                style        = Stroke(
                                    width      = 2.dp.toPx(),
                                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
                                )
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("↓", fontSize = 40.sp, color = c.purpleNeon.copy(alpha = dragBorderAlpha), fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            appStrings.dropToScan,
                            color         = c.purpleNeon.copy(alpha = dragBorderAlpha),
                            fontSize      = 16.sp,
                            fontWeight    = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}

// ─── Upload Card (router) ─────────────────────────────────────────────────────
@Composable
private fun UploadCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    if (isDesktop) UploadCardDesktop(modifier = modifier, onClick = onClick)
    else UploadCardMobile(modifier = modifier, onClick = onClick)
}

// ─── Upload Card Mobile ───────────────────────────────────────────────────────
@Composable
private fun UploadCardMobile(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val c = appColors
    val s = appStrings
    var hovered by remember { mutableStateOf(false) }
    val borderAlpha by animateFloatAsState(
        targetValue   = if (hovered) 0.6f else 0.3f,
        animationSpec = tween(200),
        label         = "border"
    )
    Box(
        modifier = modifier.fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(c.surface)
            .drawBehind {
                val path = Path().apply {
                    addRoundRect(
                        androidx.compose.ui.geometry.RoundRect(
                            left = 1f, top = 1f,
                            right = size.width - 1f, bottom = size.height - 1f,
                            cornerRadius = CornerRadius(16.dp.toPx())
                        )
                    )
                }
                drawPath(
                    path  = path,
                    color = c.purpleGlow.copy(alpha = borderAlpha),
                    style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f))
                )
            }
            .clickable(remember { MutableInteractionSource() }, null) { hovered = true; onClick() }
            .padding(vertical = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(50.dp).clip(CircleShape)
                    .background(c.uploadIconBg)
                    .drawBehind { drawCircle(c.purpleGlow.copy(alpha = 0.3f), style = Stroke(1f)) },
                contentAlignment = Alignment.Center
            ) {
                Text("↑", fontSize = 22.sp, color = c.purpleNeon, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(14.dp))
            Text(s.uploadTitle, color = c.silverText, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
            Spacer(Modifier.height(4.dp))
            Text(s.uploadSubtitleMobile, color = c.dimText, fontSize = 13.sp)
        }
    }
}

// ─── Upload Card Desktop ──────────────────────────────────────────────────────
// FIX PRINCIPAL: maxWidth folosit DOAR in BoxWithConstraints.
// Toate valorile calculate (Dp, TextUnit, Boolean) sunt pasate ca parametri
// catre functiile ajutatoare — ele nu mai au nevoie de receiver BoxWithConstraintsScope.
@Composable
private fun UploadCardDesktop(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val c           = appColors
    val interaction = remember { MutableInteractionSource() }
    val isHovered   by interaction.collectIsHoveredAsState()
    val rimAlpha    by animateFloatAsState(
        targetValue   = if (isHovered) 1f else 0.68f,
        animationSpec = tween(320),
        label         = "rim"
    )
    val lift by animateFloatAsState(
        targetValue   = if (isHovered) 1f else 0f,
        animationSpec = tween(320),
        label         = "lift"
    )
    val innerSurfaceMix = if (c.isDark) 0.42f else 0.88f

    BoxWithConstraints(modifier = modifier.graphicsLayer { translationY = -4f * lift }) {
        // Toate calculele cu maxWidth — EXCLUSIV in acest bloc
        val compact: Boolean    = maxWidth < 420.dp
        val outerShape          = RoundedCornerShape(if (compact) 18.dp else 22.dp)
        val innerShape          = RoundedCornerShape(if (compact) 16.dp else 20.dp)
        val padH: Dp            = when { maxWidth < 320.dp -> 12.dp; maxWidth < 480.dp -> 16.dp; else -> 22.dp }
        val padV: Dp            = if (maxWidth < 320.dp) 14.dp else 20.dp
        val iconSize: Dp        = when { maxWidth < 340.dp -> 48.dp; compact -> 56.dp; else -> 72.dp }
        val iconCorner: Dp      = if (compact) 16.dp else 20.dp
        val iconFont: TextUnit  = when { maxWidth < 340.dp -> 22.sp; compact -> 26.sp; else -> 30.sp }
        val titleSize: TextUnit = when { maxWidth < 340.dp -> 13.sp; compact -> 14.sp; else -> 18.sp }
        val bodySize: TextUnit  = when { maxWidth < 340.dp -> 11.sp; compact -> 12.sp; else -> 13.sp }
        val chipSpacing: Dp     = if (compact) 6.dp else 8.dp
        val smallFont: Boolean  = maxWidth < 340.dp
        val btnFraction: Float  = if (maxWidth < 300.dp) 1f else 0.85f

        val chipLabels = listOf("PDF", "Images", "Documents")

        Box(Modifier.fillMaxWidth()) {
            Box(
                Modifier.fillMaxWidth().clip(outerShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                c.purpleGlow.copy(alpha = 0.28f + 0.32f * rimAlpha),
                                c.goldShine.copy(alpha  = 0.22f + 0.42f * rimAlpha),
                                c.purpleNeon.copy(alpha = 0.26f + 0.34f * rimAlpha)
                            )
                        )
                    )
                    .padding(2.dp)
            ) {
                Box(
                    Modifier.fillMaxWidth().clip(innerShape)
                        .background(Brush.verticalGradient(listOf(c.surface, c.purpleDim.copy(alpha = innerSurfaceMix))))
                        .drawBehind {
                            val r = (minOf(size.width, size.height) * 0.35f).coerceAtLeast(48.dp.toPx())
                            drawCircle(
                                brush  = Brush.radialGradient(
                                    colors = listOf(c.purpleGlow.copy(alpha = 0.2f * rimAlpha), Color.Transparent),
                                    center = Offset(size.width * 0.5f, size.height * 0.12f),
                                    radius = r
                                ),
                                radius = r,
                                center = Offset(size.width * 0.5f, size.height * 0.12f)
                            )
                        }
                        .hoverable(interaction)
                        .clickable(interaction, null) { onClick() }
                        .padding(horizontal = padH, vertical = padV)
                ) {
                    if (compact) {
                        Column(
                            modifier            = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Valori pasate explicit ca parametri simpli
                            UploadIcon(size = iconSize, corner = iconCorner, fontSize = iconFont)
                            Spacer(Modifier.height(12.dp))
                            UploadTitleBlock(titleSize = titleSize, bodySize = bodySize, compact = true)
                            Spacer(Modifier.height(12.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(chipSpacing),
                                modifier              = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                chipLabels.forEach { TypeChip(label = it, smallFont = smallFont) }
                            }
                            Spacer(Modifier.height(14.dp))
                            BrowseButton(compact = true, modifier = Modifier.fillMaxWidth(btnFraction))
                        }
                    } else {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            UploadIcon(size = iconSize, corner = iconCorner, fontSize = iconFont)
                            Spacer(Modifier.width(20.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                UploadTitleBlock(titleSize = titleSize, bodySize = bodySize, compact = false)
                                Spacer(Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(chipSpacing)) {
                                    chipLabels.forEach { TypeChip(label = it, smallFont = smallFont) }
                                }
                            }
                            Spacer(Modifier.width(14.dp))
                            BrowseButton(compact = false)
                        }
                    }
                }
            }
        }
    }
}

// ─── Componente ajutatoare ────────────────────────────────────────────────────
// Primesc valori concrete (Dp, TextUnit, Boolean) — nu acceseaza maxWidth.
// Aceasta e cauza exacta a erorii originale: functiile accesau maxWidth
// fara a fi in interiorul unui BoxWithConstraints scope.

@Composable
private fun UploadIcon(size: Dp, corner: Dp, fontSize: TextUnit) {
    val c = appColors
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(corner))
            .background(Brush.linearGradient(listOf(c.purpleCore, c.purpleBright.copy(alpha = 0.92f))))
            .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(corner)),
        contentAlignment = Alignment.Center
    ) {
        Text("↑", fontSize = fontSize, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun UploadTitleBlock(titleSize: TextUnit, bodySize: TextUnit, compact: Boolean) {
    val c     = appColors
    val s     = appStrings
    val align = if (compact) TextAlign.Center else TextAlign.Start
    Text(
        s.uploadTitle,
        style = TextStyle(
            brush         = Brush.linearGradient(listOf(c.silverText, c.heroGradientEnd)),
            fontSize      = titleSize,
            fontWeight    = FontWeight.Bold,
            letterSpacing = if (compact) 0.5.sp else 1.sp
        ),
        textAlign = align
    )
    Spacer(Modifier.height(if (compact) 6.dp else 8.dp))
    Text(
        s.uploadSubtitleDesktop,
        color      = c.dimText,
        fontSize   = bodySize,
        lineHeight = bodySize * 1.35f,
        textAlign  = align
    )
}

@Composable
private fun TypeChip(label: String, smallFont: Boolean) {
    val c = appColors
    Box(
        Modifier.clip(RoundedCornerShape(8.dp)).background(c.glassWhite).padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(label, color = c.dimText, fontSize = if (smallFont) 9.sp else 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.2.sp)
    }
}

@Composable
private fun BrowseButton(compact: Boolean, modifier: Modifier = Modifier) {
    val c = appColors
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.horizontalGradient(listOf(c.goldShine, c.goldShine.copy(alpha = 0.78f))))
            .padding(horizontal = 32.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(appStrings.browseBtn, color = Color(0xFF1A0F00), fontSize = if (compact) 12.sp else 14.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.7.sp)
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
                drawRoundRect(color = c.scanGlowBorder.copy(alpha = 0.25f), cornerRadius = CornerRadius(20.dp.toPx()), style = Stroke(1.5f))
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
            Text(appStrings.navScan, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 4.sp)
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
            modifier = Modifier.size(16.dp).align(Alignment.Center).clip(CircleShape).border(2.dp, color, CircleShape)
        )
    }
}

// ─── Bottom Nav ───────────────────────────────────────────────────────────────
@Composable
private fun HomeBottomNav(selected: String, onHistory: () -> Unit, onFiles: () -> Unit) {
    val c = appColors
    val s = appStrings
    data class NavItem(val id: String, val icon: String, val label: String, val onClick: () -> Unit)

    val items = listOf(
        NavItem("history", "◷", s.navHistory, onHistory),
        NavItem("scan",    "⊙", s.navScan,    {}),
        NavItem("files",   "⊟", s.navFiles,   onFiles),
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(c.navBarBg)
            .drawBehind { drawLine(c.navBorderLine, Offset(0f, 0f), Offset(size.width, 0f), 1f) }
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEach { item ->
            val isSelected = item.id == selected
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
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
                    item.label,
                    fontSize      = 9.sp,
                    letterSpacing = 1.2.sp,
                    fontWeight    = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color         = if (isSelected) c.purpleNeon else c.dimText
                )
            }
        }
    }
}
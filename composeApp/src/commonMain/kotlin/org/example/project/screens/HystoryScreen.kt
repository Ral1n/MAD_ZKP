package org.example.project.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// ─── Paleta ───────────────────────────────────────────────────────────────────
private val Void         = Color(0xFF060608)
private val NightSurface = Color(0xFF12121C)
private val GlassWhite   = Color(0x0FFFFFFF)
private val GlassBorder  = Color(0x18FFFFFF)
private val PurpleCore   = Color(0xFF7C3AED)
private val PurpleGlow   = Color(0xFF9F67FF)
private val PurpleNeon   = Color(0xFFBF9FFF)
private val PurpleDim    = Color(0xFF2D1B5E)
private val CrimsonGlow  = Color(0xFFE53935)
private val EmeraldGlow  = Color(0xFF10B981)
private val GoldShine    = Color(0xFFFFBD2E)
private val SilverText   = Color(0xFFE2E2F0)
private val DimText      = Color(0xFF6B6B8A)
private val GhostText    = Color(0xFF35354A)

// ─── Model ────────────────────────────────────────────────────────────────────
data class AccessEntry(
    val id: String,
    val appName: String,
    val appInitials: String,
    val documentType: String,
    val keyId: String,
    val accessedAt: String,
    val isRevoked: Boolean = false,
    val accentColor: Color = PurpleCore,
    val usageCount: Int = 1
)

private val sampleHistory = listOf(
    AccessEntry("1", "Bolt Food",       "BF", "Buletin",  "#a3f2c1", "Azi, 14:32",    false, Color(0xFF00C853), 3),
    AccessEntry("2", "eMAG",            "eM", "Buletin",  "#b1d3a2", "Azi, 11:05",    false, Color(0xFFE53935), 1),
    AccessEntry("3", "Glovo",           "GL", "Pașaport", "#c9e1f0", "Ieri, 20:14",   false, Color(0xFFFF6D00), 2),
    AccessEntry("4", "Untold Festival", "UF", "Buletin",  "#d4a1bc", "3 Apr, 18:00",  true,  Color(0xFF7C3AED), 1),
    AccessEntry("5", "PayU",            "PU", "Permis",   "#e2b345", "2 Apr, 09:22",  false, Color(0xFF1565C0), 4),
    AccessEntry("6", "Tazz",            "TZ", "Buletin",  "#f1c2d3", "1 Apr, 16:45",  true,  Color(0xFFAD1457), 1),
    AccessEntry("7", "Revolut",         "RV", "Pașaport", "#a8d5e2", "31 Mar, 12:30", false, Color(0xFF0097A7), 2),
    AccessEntry("8", "Elefant.ro",      "EL", "Diplomă",  "#c3b1e1", "30 Mar, 10:10", false, Color(0xFF6A1B9A), 1),
)

// ─── Screen ───────────────────────────────────────────────────────────────────
@Composable
fun HistoryScreen(onSettingsClick: () -> Unit = {}) {
    var historyList by remember { mutableStateOf(sampleHistory) }
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Active", "Revoked")

    val filtered = when (selectedFilter) {
        "Active"  -> historyList.filter { !it.isRevoked }
        "Revoked" -> historyList.filter { it.isRevoked }
        else      -> historyList
    }

    val infiniteTransition = rememberInfiniteTransition(label = "ambient")
    val ambientAlpha by infiniteTransition.animateFloat(
        initialValue = 0.04f, targetValue = 0.11f,
        animationSpec = infiniteRepeatable(tween(4000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "ambient_alpha"
    )

    Box(modifier = Modifier.fillMaxSize().background(Void)) {
        // Ambient glow blobs
        Box(
            modifier = Modifier.size(320.dp).offset(x = (-90).dp, y = 50.dp)
                .blur(130.dp)
                .background(PurpleCore.copy(alpha = ambientAlpha), CircleShape)
        )
        Box(
            modifier = Modifier.size(220.dp).align(Alignment.BottomEnd)
                .offset(x = 70.dp, y = (-80).dp)
                .blur(110.dp)
                .background(Color(0xFF1A0A4E).copy(alpha = ambientAlpha * 1.6f), CircleShape)
        )

        Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
            HeaderBar(onSettingsClick)
            HeroSection()
            Spacer(Modifier.height(24.dp))
            FilterRow(filters, selectedFilter) { selectedFilter = it }
            Spacer(Modifier.height(14.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                itemsIndexed(filtered, key = { _, e -> e.id }) { index, entry ->
                    var visible by remember { mutableStateOf(false) }
                    LaunchedEffect(entry.id) { delay(index * 55L); visible = true }
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(380)) + slideInHorizontally(tween(380, easing = EaseOutQuart)) { it / 3 }
                    ) {
                        AccessCard(entry) {
                            historyList = historyList.map {
                                if (it.id == entry.id) it.copy(isRevoked = true) else it
                            }
                        }
                    }
                }
            }
            BottomNavBar(selected = "history")
        }
    }
}

// ─── Header ───────────────────────────────────────────────────────────────────
@Composable
private fun HeaderBar(onSettingsClick: () -> Unit) {
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
            text = "History",
            style = TextStyle(
                brush = Brush.linearGradient(listOf(PurpleGlow, PurpleNeon)),
                fontSize = 18.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp
            )
        )
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                .background(GlassWhite)
                .drawBehind {
                    drawRoundRect(GlassBorder, cornerRadius = CornerRadius(10.dp.toPx()), style = Stroke(1f))
                }
                .clickable(remember { MutableInteractionSource() }, null) { onSettingsClick() },
            contentAlignment = Alignment.Center
        ) { Text("⚙", fontSize = 16.sp, color = PurpleNeon) }
    }
}

// ─── Hero ─────────────────────────────────────────────────────────────────────
@Composable
private fun HeroSection() {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(80); visible = true }
    AnimatedVisibility(visible, enter = fadeIn(tween(700)) + slideInVertically(tween(700, easing = EaseOutQuart)) { -30 }) {
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = "who used",
                style = TextStyle(
                    brush = Brush.linearGradient(listOf(Color.White, Color(0xFFCCBBFF))),
                    fontSize = 34.sp, fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic, lineHeight = 40.sp
                )
            )
            Text(
                text = "your keys",
                style = TextStyle(
                    brush = Brush.linearGradient(listOf(PurpleGlow, PurpleNeon)),
                    fontSize = 34.sp, fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic, lineHeight = 40.sp
                )
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(width = 18.dp, height = 1.dp)
                    .background(Brush.horizontalGradient(listOf(Color.Transparent, GoldShine))))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "audit trail complet · end-to-end encrypted",
                    color = GoldShine, fontSize = 11.sp,
                    fontWeight = FontWeight.Medium, letterSpacing = 0.4.sp
                )
            }
        }
    }
}

// ─── Filters ──────────────────────────────────────────────────────────────────
@Composable
private fun FilterRow(filters: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        filters.forEach { filter ->
            val isSelected = filter == selected
            val bg by animateColorAsState(
                if (isSelected) PurpleCore else NightSurface, tween(220), label = "f"
            )
            Box(
                modifier = Modifier.clip(RoundedCornerShape(24.dp)).background(bg)
                    .drawBehind {
                        if (!isSelected) drawRoundRect(GlassBorder, cornerRadius = CornerRadius(24.dp.toPx()), style = Stroke(1f))
                    }
                    .clickable(remember { MutableInteractionSource() }, null) { onSelect(filter) }
                    .padding(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Text(
                    filter,
                    color = if (isSelected) Color.White else DimText,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    letterSpacing = 0.3.sp
                )
            }
        }
        Spacer(Modifier.weight(1f))
    }
}

// ─── Card ─────────────────────────────────────────────────────────────────────
@Composable
private fun AccessCard(entry: AccessEntry, onRevoke: () -> Unit) {
    var showConfirm by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_${entry.id}")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1300, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
            .background(NightSurface)
            .drawBehind {
                drawRoundRect(entry.accentColor.copy(alpha = 0.14f), cornerRadius = CornerRadius(18.dp.toPx()), style = Stroke(1f))
                drawLine(
                    color = entry.accentColor.copy(alpha = if (entry.isRevoked) 0.3f else 0.75f),
                    start = Offset(0f, 20.dp.toPx()),
                    end = Offset(0f, size.height - 20.dp.toPx()),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 14.dp, top = 14.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp))
                    .background(Brush.radialGradient(listOf(
                        entry.accentColor.copy(alpha = 0.3f),
                        entry.accentColor.copy(alpha = 0.07f)
                    )))
                    .drawBehind {
                        drawRoundRect(entry.accentColor.copy(alpha = 0.3f), cornerRadius = CornerRadius(14.dp.toPx()), style = Stroke(1f))
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(entry.appInitials, color = entry.accentColor, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        entry.appName, color = SilverText, fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold, maxLines = 1,
                        overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, false)
                    )
                    if (entry.usageCount > 1) {
                        Spacer(Modifier.width(6.dp))
                        Box(Modifier.clip(RoundedCornerShape(6.dp)).background(PurpleDim).padding(horizontal = 6.dp, vertical = 2.dp)) {
                            Text("×${entry.usageCount}", color = PurpleNeon, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val dotColor = if (entry.isRevoked) CrimsonGlow else EmeraldGlow
                    Box(Modifier.size(7.dp).clip(CircleShape)
                        .background(if (!entry.isRevoked) dotColor.copy(alpha = pulseAlpha) else dotColor))
                    Spacer(Modifier.width(6.dp))
                    Text(entry.documentType, color = PurpleNeon, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text(" · ", color = GhostText, fontSize = 12.sp)
                    Text(entry.accessedAt, color = DimText, fontSize = 12.sp)
                }
                Spacer(Modifier.height(3.dp))
                Text("cheie ${entry.keyId}", color = GhostText, fontSize = 10.sp, letterSpacing = 0.3.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            Spacer(Modifier.width(10.dp))

            if (entry.isRevoked) {
                Box(
                    Modifier.clip(RoundedCornerShape(10.dp))
                        .background(CrimsonGlow.copy(alpha = 0.1f))
                        .drawBehind { drawRoundRect(CrimsonGlow.copy(alpha = 0.25f), cornerRadius = CornerRadius(10.dp.toPx()), style = Stroke(1f)) }
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                ) { Text("Revocat", color = CrimsonGlow, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
            } else {
                AnimatedContent(showConfirm, transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) }, label = "action") { confirming ->
                    if (confirming) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                Modifier.clip(RoundedCornerShape(10.dp))
                                    .background(CrimsonGlow.copy(alpha = 0.14f))
                                    .drawBehind { drawRoundRect(CrimsonGlow.copy(alpha = 0.4f), cornerRadius = CornerRadius(10.dp.toPx()), style = Stroke(1f)) }
                                    .clickable(remember { MutableInteractionSource() }, null) { showConfirm = false; onRevoke() }
                                    .padding(horizontal = 10.dp, vertical = 7.dp)
                            ) { Text("Da", color = CrimsonGlow, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            Box(
                                Modifier.clip(RoundedCornerShape(10.dp)).background(GlassWhite)
                                    .clickable(remember { MutableInteractionSource() }, null) { showConfirm = false }
                                    .padding(horizontal = 10.dp, vertical = 7.dp)
                            ) { Text("Nu", color = DimText, fontSize = 11.sp) }
                        }
                    } else {
                        Box(
                            Modifier.clip(RoundedCornerShape(10.dp)).background(PurpleDim)
                                .drawBehind { drawRoundRect(PurpleCore.copy(alpha = 0.4f), cornerRadius = CornerRadius(10.dp.toPx()), style = Stroke(1f)) }
                                .clickable(remember { MutableInteractionSource() }, null) { showConfirm = true }
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                        ) { Text("Revocă", color = PurpleNeon, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    }
                }
            }
        }
    }
}

// ─── Bottom Nav ───────────────────────────────────────────────────────────────
@Composable
fun BottomNavBar(selected: String) {
    val items = listOf(Triple("history", "◷", "HISTORY"), Triple("scan", "⊙", "SCAN"), Triple("files", "⊟", "FILES"))
    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(NightSurface)
            .drawBehind { drawLine(GlassBorder, Offset(0f, 0f), Offset(size.width, 0f), 1f) }
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEach { (id, icon, label) ->
            val isSelected = id == selected
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 20.dp)) {
                if (isSelected) {
                    Box(Modifier.size(width = 24.dp, height = 2.dp).background(
                        Brush.horizontalGradient(listOf(Color.Transparent, PurpleGlow, Color.Transparent)),
                        RoundedCornerShape(1.dp)
                    ))
                    Spacer(Modifier.height(4.dp))
                } else {
                    Spacer(Modifier.height(6.dp))
                }
                Text(icon, fontSize = 22.sp, color = if (isSelected) PurpleGlow else DimText)
                Spacer(Modifier.height(3.dp))
                Text(
                    label, fontSize = 9.sp, letterSpacing = 1.2.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) PurpleNeon else DimText
                )
            }
        }
    }
}
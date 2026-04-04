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
import org.example.project.theme.appColors

// ─────────────────────────────────────────────────────────────────────────────
// MODEL
// ─────────────────────────────────────────────────────────────────────────────
enum class DocumentStatus {
    UPLOADED,
    WAITING,
    APPROVED
}

data class DocumentFile(
    val id: String,
    val fileName: String,
    val documentType: String,
    val status: DocumentStatus,
    val uploadedAt: String,
    val fileSizeKb: Int,
    val encryptedStorageKey: String,
    val thumbnailInitials: String,
    val approvedAt: String? = null,
    val approvedBy: String? = null,
    val waitingSince: String? = null
)

// ─────────────────────────────────────────────────────────────────────────────
// REPOSITORY INTERFACE
// ─────────────────────────────────────────────────────────────────────────────
interface FilesRepository {
    suspend fun getAllFiles(): List<DocumentFile>
    suspend fun deleteFile(id: String)
}

// ─────────────────────────────────────────────────────────────────────────────
// DATE MOCK
// ─────────────────────────────────────────────────────────────────────────────
private val mockFiles = listOf(
    DocumentFile(
        id = "uuid-001", fileName = "Buletin_Ion_Popescu.pdf",
        documentType = "Buletin", status = DocumentStatus.APPROVED,
        uploadedAt = "Azi, 14:32", fileSizeKb = 1240,
        encryptedStorageKey = "enc::a3f2c1d4e5", thumbnailInitials = "BP",
        approvedAt = "Azi, 15:00", approvedBy = "Bolt Food"
    ),
    DocumentFile(
        id = "uuid-002", fileName = "Pasaport_2024.jpg",
        documentType = "Pașaport", status = DocumentStatus.WAITING,
        uploadedAt = "Azi, 11:05", fileSizeKb = 3800,
        encryptedStorageKey = "enc::b1d3a2f9c8", thumbnailInitials = "PA",
        waitingSince = "Azi, 11:05"
    ),
    DocumentFile(
        id = "uuid-003", fileName = "Diploma_Licenta.pdf",
        documentType = "Diplomă", status = DocumentStatus.UPLOADED,
        uploadedAt = "Ieri, 20:14", fileSizeKb = 5200,
        encryptedStorageKey = "enc::c9e1f023ab", thumbnailInitials = "DL"
    ),
    DocumentFile(
        id = "uuid-004", fileName = "Permis_Auto_B.pdf",
        documentType = "Permis", status = DocumentStatus.APPROVED,
        uploadedAt = "3 Apr, 18:00", fileSizeKb = 980,
        encryptedStorageKey = "enc::d4a1bc77ef", thumbnailInitials = "PA",
        approvedAt = "3 Apr, 19:30", approvedBy = "PayU"
    ),
    DocumentFile(
        id = "uuid-005", fileName = "Certificat_Nastere.pdf",
        documentType = "Certificat", status = DocumentStatus.WAITING,
        uploadedAt = "2 Apr, 09:22", fileSizeKb = 720,
        encryptedStorageKey = "enc::e2b34512cd", thumbnailInitials = "CN",
        waitingSince = "2 Apr, 09:22"
    ),
    DocumentFile(
        id = "uuid-006", fileName = "Scan_Factura_eMAG.jpg",
        documentType = "Factură", status = DocumentStatus.UPLOADED,
        uploadedAt = "1 Apr, 16:45", fileSizeKb = 450,
        encryptedStorageKey = "enc::f1c2d3a4b5", thumbnailInitials = "FE"
    ),
)

// ─────────────────────────────────────────────────────────────────────────────
// SCREEN
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun FilesScreen(
    onNavigateToScan: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    isDarkTheme: Boolean = true,
    onToggleTheme: () -> Unit = {},
    repository: FilesRepository? = null
) {
    val c = appColors

    var filesList by remember { mutableStateOf(mockFiles) }
    var selectedFilter by remember { mutableStateOf("Toate") }
    val filters = listOf("Toate", "Încărcate", "În așteptare", "Aprobate")

    // Decomentează când ai repository real:
    // LaunchedEffect(Unit) {
    //     repository?.getAllFiles()?.let { filesList = it }
    // }

    val filtered = when (selectedFilter) {
        "Încărcate"    -> filesList.filter { it.status == DocumentStatus.UPLOADED }
        "În așteptare" -> filesList.filter { it.status == DocumentStatus.WAITING }
        "Aprobate"     -> filesList.filter { it.status == DocumentStatus.APPROVED }
        else           -> filesList
    }

    val infiniteTransition = rememberInfiniteTransition(label = "ambient")
    val ambientAlpha by infiniteTransition.animateFloat(
        initialValue = 0.04f, targetValue = 0.11f,
        animationSpec = infiniteRepeatable(tween(4000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "ambient_alpha"
    )

    Box(modifier = Modifier.fillMaxSize().background(c.background)) {

        Box(
            Modifier.size(300.dp).offset(x = (-80).dp, y = 60.dp).blur(130.dp)
                .background(c.ambientBlob1.copy(alpha = ambientAlpha), CircleShape)
        )
        Box(
            Modifier.size(200.dp).align(Alignment.BottomEnd).offset(x = 60.dp, y = (-100).dp).blur(110.dp)
                .background(c.ambientBlob2.copy(alpha = ambientAlpha * 1.6f), CircleShape)
        )

        Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {

            FilesTopBar(isDarkTheme = isDarkTheme, onToggleTheme = onToggleTheme)

            FilesHeroSection(
                totalCount    = filesList.size,
                approvedCount = filesList.count { it.status == DocumentStatus.APPROVED }
            )

            Spacer(Modifier.height(20.dp))

            FilesFilterRow(filters, selectedFilter) { selectedFilter = it }

            Spacer(Modifier.height(14.dp))

            if (filtered.isEmpty()) {
                EmptyState(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    itemsIndexed(filtered, key = { _, f -> f.id }) { index, file ->
                        var visible by remember { mutableStateOf(false) }
                        LaunchedEffect(file.id) { delay(index * 55L); visible = true }
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(380)) + slideInHorizontally(tween(380, easing = EaseOutQuart)) { it / 3 }
                        ) {
                            DocumentCard(
                                file     = file,
                                onDelete = {
                                    filesList = filesList.filter { it.id != file.id }
                                    // Cu repository real:
                                    // coroutineScope.launch { repository?.deleteFile(file.id) }
                                }
                            )
                        }
                    }
                }
            }

            // ── Bottom Nav cu navigare completă ──────────────────────────────
            FilesBottomNav(
                onNavigateToScan    = onNavigateToScan,
                onNavigateToHistory = onNavigateToHistory
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TOP BAR
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FilesTopBar(isDarkTheme: Boolean, onToggleTheme: () -> Unit) {
    val c = appColors
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.size(36.dp))
        Spacer(Modifier.weight(1f))
        Text(
            text = "Files",
            style = TextStyle(
                brush = Brush.linearGradient(listOf(c.purpleGlow, c.purpleNeon)),
                fontSize = 18.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp
            )
        )
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
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

// ─────────────────────────────────────────────────────────────────────────────
// HERO
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FilesHeroSection(totalCount: Int, approvedCount: Int) {
    val c = appColors
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(80); visible = true }

    AnimatedVisibility(
        visible,
        enter = fadeIn(tween(700)) + slideInVertically(tween(700, easing = EaseOutQuart)) { -30 }
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = "documentele",
                style = TextStyle(
                    brush = Brush.linearGradient(listOf(c.silverText, c.heroGradientEnd)),
                    fontSize = 34.sp, fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic, lineHeight = 40.sp
                )
            )
            Text(
                text = "tale sigure",
                style = TextStyle(
                    brush = Brush.linearGradient(listOf(c.purpleGlow, c.purpleNeon)),
                    fontSize = 34.sp, fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic, lineHeight = 40.sp
                )
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatChip(label = "Total",    value = "$totalCount",    color = c.purpleNeon)
                StatChip(label = "Aprobate", value = "$approvedCount", color = c.emeraldGlow)
                StatChip(label = "Criptate", value = "100%",           color = c.goldShine)
            }
        }
    }
}

@Composable
private fun StatChip(label: String, value: String, color: Color) {
    val c = appColors
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(c.surface)
            .drawBehind {
                drawRoundRect(color.copy(alpha = 0.3f), cornerRadius = CornerRadius(12.dp.toPx()), style = Stroke(1f))
            }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(value, color = color, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            Text(label, color = c.dimText, fontSize = 11.sp)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// FILTER ROW
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FilesFilterRow(filters: List<String>, selected: String, onSelect: (String) -> Unit) {
    val c = appColors
    androidx.compose.foundation.lazy.LazyRow(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters.size) { index ->
            val filter = filters[index]
            val isSelected = filter == selected
            val bg by animateColorAsState(
                if (isSelected) c.purpleCore else c.surface, tween(220), label = "filter_bg"
            )
            Box(
                modifier = Modifier.clip(RoundedCornerShape(24.dp)).background(bg)
                    .drawBehind {
                        if (!isSelected) drawRoundRect(c.glassBorder, cornerRadius = CornerRadius(24.dp.toPx()), style = Stroke(1f))
                    }
                    .clickable(remember { MutableInteractionSource() }, null) { onSelect(filter) }
                    .padding(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Text(
                    filter,
                    color = if (isSelected) Color.White else c.dimText,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    letterSpacing = 0.3.sp
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DOCUMENT CARD
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun DocumentCard(file: DocumentFile, onDelete: () -> Unit) {
    val c = appColors
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val statusColor = when (file.status) {
        DocumentStatus.APPROVED -> c.emeraldGlow
        DocumentStatus.WAITING  -> c.goldShine
        DocumentStatus.UPLOADED -> c.purpleNeon
    }
    val statusLabel = when (file.status) {
        DocumentStatus.APPROVED -> "Aprobat"
        DocumentStatus.WAITING  -> "În așteptare"
        DocumentStatus.UPLOADED -> "Încărcat"
    }
    val statusIcon = when (file.status) {
        DocumentStatus.APPROVED -> "✓"
        DocumentStatus.WAITING  -> "⏳"
        DocumentStatus.UPLOADED -> "↑"
    }

    val accentColor = statusColor

    val infiniteTransition = rememberInfiniteTransition(label = "dot_${file.id}")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "dot"
    )

    // ── FIX: calculăm dimensiunea separat, fără lambda în string template ────
    val sizeText = if (file.fileSizeKb >= 1024) {
        "%.1f MB".format(file.fileSizeKb / 1024f)
    } else {
        "${file.fileSizeKb} KB"
    }

    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
            .background(c.surface)
            .drawBehind {
                drawRoundRect(
                    accentColor.copy(alpha = 0.13f),
                    cornerRadius = CornerRadius(18.dp.toPx()),
                    style = Stroke(1f)
                )
                drawLine(
                    color = accentColor.copy(
                        alpha = if (file.status == DocumentStatus.WAITING) dotAlpha * 0.8f else 0.75f
                    ),
                    start = Offset(0f, 20.dp.toPx()),
                    end   = Offset(0f, size.height - 20.dp.toPx()),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 14.dp, top = 14.dp, bottom = 14.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                // Avatar
                Box(
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.radialGradient(listOf(
                                accentColor.copy(alpha = 0.25f),
                                accentColor.copy(alpha = 0.06f)
                            ))
                        )
                        .drawBehind {
                            drawRoundRect(accentColor.copy(alpha = 0.3f), cornerRadius = CornerRadius(14.dp.toPx()), style = Stroke(1f))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(file.thumbnailInitials, color = accentColor, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                }

                Spacer(Modifier.width(14.dp))

                // Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        file.fileName, color = c.silverText, fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold, maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(7.dp).clip(CircleShape).background(
                                accentColor.copy(
                                    alpha = if (file.status == DocumentStatus.WAITING) dotAlpha else 1f
                                )
                            )
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(file.documentType, color = c.purpleNeon, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text(" · ", color = c.ghostText, fontSize = 12.sp)
                        Text(file.uploadedAt, color = c.dimText, fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(3.dp))
                    // ── FIX: folosim variabila sizeText calculată mai sus ────
                    Text(
                        "$sizeText · 🔒 criptat",
                        color = c.ghostText, fontSize = 10.sp, letterSpacing = 0.3.sp
                    )
                }

                Spacer(Modifier.width(10.dp))

                // Status badge + delete
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(accentColor.copy(alpha = 0.12f))
                            .drawBehind {
                                drawRoundRect(accentColor.copy(alpha = 0.3f), cornerRadius = CornerRadius(10.dp.toPx()), style = Stroke(1f))
                            }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(statusIcon,  fontSize = 10.sp, color = accentColor)
                            Text(statusLabel, color = accentColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    AnimatedContent(
                        showDeleteConfirm,
                        transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(180)) },
                        label = "del_${file.id}"
                    ) { confirming ->
                        if (confirming) {
                            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                Box(
                                    Modifier.clip(RoundedCornerShape(8.dp))
                                        .background(c.crimsonGlow.copy(alpha = 0.14f))
                                        .drawBehind {
                                            drawRoundRect(c.crimsonGlow.copy(alpha = 0.4f), cornerRadius = CornerRadius(8.dp.toPx()), style = Stroke(1f))
                                        }
                                        .clickable(remember { MutableInteractionSource() }, null) {
                                            showDeleteConfirm = false; onDelete()
                                        }
                                        .padding(horizontal = 8.dp, vertical = 5.dp)
                                ) { Text("Da", color = c.crimsonGlow, fontSize = 10.sp, fontWeight = FontWeight.Bold) }

                                Box(
                                    Modifier.clip(RoundedCornerShape(8.dp)).background(c.glassWhite)
                                        .clickable(remember { MutableInteractionSource() }, null) { showDeleteConfirm = false }
                                        .padding(horizontal = 8.dp, vertical = 5.dp)
                                ) { Text("Nu", color = c.dimText, fontSize = 10.sp) }
                            }
                        } else {
                            Box(
                                Modifier.clip(RoundedCornerShape(8.dp)).background(c.glassWhite)
                                    .drawBehind {
                                        drawRoundRect(c.glassBorder, cornerRadius = CornerRadius(8.dp.toPx()), style = Stroke(1f))
                                    }
                                    .clickable(remember { MutableInteractionSource() }, null) { showDeleteConfirm = true }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            ) { Text("🗑", fontSize = 12.sp) }
                        }
                    }
                }
            }

            // Banner APPROVED
            AnimatedVisibility(
                visible = file.status == DocumentStatus.APPROVED && file.approvedBy != null,
                enter = fadeIn(tween(300)) + expandVertically(tween(300))
            ) {
                Column {
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(c.emeraldGlow.copy(alpha = 0.06f))
                            .drawBehind {
                                drawRoundRect(c.emeraldGlow.copy(alpha = 0.2f), cornerRadius = CornerRadius(10.dp.toPx()), style = Stroke(1f))
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("✓", color = c.emeraldGlow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Aprobat de ${file.approvedBy} · ${file.approvedAt}",
                                color = c.emeraldGlow, fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Banner WAITING
            AnimatedVisibility(
                visible = file.status == DocumentStatus.WAITING,
                enter = fadeIn(tween(300)) + expandVertically(tween(300))
            ) {
                Column {
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(c.goldShine.copy(alpha = 0.06f))
                            .drawBehind {
                                drawRoundRect(c.goldShine.copy(alpha = 0.2f), cornerRadius = CornerRadius(10.dp.toPx()), style = Stroke(1f))
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⏳", fontSize = 11.sp)
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Trimis pentru aprobare · ${file.waitingSince ?: ""}",
                                color = c.goldShine, fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// EMPTY STATE
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    val c = appColors
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(72.dp).clip(CircleShape)
                    .background(c.purpleDim)
                    .drawBehind { drawCircle(c.purpleCore.copy(alpha = 0.3f), style = Stroke(1f)) },
                contentAlignment = Alignment.Center
            ) { Text("⊟", fontSize = 32.sp, color = c.purpleNeon) }
            Spacer(Modifier.height(16.dp))
            Text("Niciun document găsit", color = c.silverText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text("Încearcă alt filtru sau încarcă un fișier", color = c.dimText, fontSize = 13.sp)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// BOTTOM NAV — navigare completă între toate cele 3 ecrane
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FilesBottomNav(
    onNavigateToScan: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val c = appColors

    data class NavItem(val id: String, val icon: String, val label: String)

    val items = listOf(
        NavItem("history", "◷", "HISTORY"),
        NavItem("scan",    "⊙", "SCAN"),
        NavItem("files",   "⊟", "FILES")
    )

    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(c.navBarBg)
            .drawBehind { drawLine(c.navBorderLine, Offset(0f, 0f), Offset(size.width, 0f), 1f) }
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEach { item ->
            val isSelected = item.id == "files"
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .clickable(remember { MutableInteractionSource() }, null) {
                        when (item.id) {
                            "scan"    -> onNavigateToScan()
                            "history" -> onNavigateToHistory()
                            // "files" -> suntem deja aici, nu facem nimic
                        }
                    }
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
package org.example.project.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.example.project.AppLanguage
import org.example.project.theme.appColors
import org.example.project.theme.appStrings

@Composable
fun ProfileScreen(
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {},
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    onLanguageChange: (AppLanguage) -> Unit = {}
) {
    val c = appColors
    val s = appStrings

    val infiniteTransition = rememberInfiniteTransition(label = "ambient_profile")
    val ambientAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.04f,
        targetValue   = 0.11f,
        animationSpec = infiniteRepeatable(tween(4000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label         = "ambient_alpha"
    )

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(60); visible = true }

    Box(modifier = Modifier.fillMaxSize().background(c.background)) {

        // ── Ambient blobs ────────────────────────────────────────────────────
        Box(
            Modifier.size(320.dp).offset(x = 80.dp, y = (-60).dp).blur(140.dp)
                .background(c.ambientBlob1.copy(alpha = ambientAlpha), CircleShape)
        )
        Box(
            Modifier.size(220.dp).align(Alignment.BottomStart).offset(x = (-60).dp, y = 80.dp).blur(120.dp)
                .background(c.ambientBlob2.copy(alpha = ambientAlpha * 1.5f), CircleShape)
        )

        Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {

            // ── Top bar ──────────────────────────────────────────────────────
            Row(
                modifier          = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(c.glassWhite)
                        .drawBehind {
                            drawRoundRect(c.glassBorder, cornerRadius = CornerRadius(10.dp.toPx()), style = Stroke(1f))
                        }
                        .clickable(remember { MutableInteractionSource() }, null) { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("←", fontSize = 18.sp, color = c.purpleNeon)
                }
                Spacer(Modifier.weight(1f))
                Text(
                    s.profileTitle,
                    style = TextStyle(
                        brush         = Brush.linearGradient(listOf(c.purpleGlow, c.purpleNeon)),
                        fontSize      = 18.sp,
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.size(36.dp))
            }

            // ── Content ──────────────────────────────────────────────────────
            AnimatedVisibility(
                visible = visible,
                enter   = fadeIn(tween(600)) + slideInVertically(tween(600, easing = FastOutSlowInEasing)) { 40 }
            ) {
                Column(
                    modifier            = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(32.dp))

                    // ── Avatar ───────────────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(c.purpleCore.copy(alpha = 0.8f), c.purpleDim)
                                )
                            )
                            .drawBehind {
                                drawCircle(
                                    brush = Brush.sweepGradient(
                                        listOf(c.purpleGlow.copy(alpha = 0.7f), c.goldShine.copy(alpha = 0.4f), c.purpleGlow.copy(alpha = 0.7f))
                                    ),
                                    radius = size.minDimension / 2 - 1.5.dp.toPx(),
                                    style  = Stroke(2.dp.toPx())
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "A",
                            color      = Color.White,
                            fontSize   = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // ── Name / email ─────────────────────────────────────────
                    Text(
                        "Admin",
                        color      = c.silverText,
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "admin@zkp.ro",
                        color    = c.dimText,
                        fontSize = 14.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    // ── Badge ────────────────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(c.purpleCore.copy(alpha = 0.18f))
                            .drawBehind {
                                drawRoundRect(c.purpleGlow.copy(alpha = 0.35f), cornerRadius = CornerRadius(20.dp.toPx()), style = Stroke(1f))
                            }
                            .padding(horizontal = 14.dp, vertical = 5.dp)
                    ) {
                        Text(
                            "Zero-Knowledge Vault",
                            color         = c.purpleNeon,
                            fontSize      = 11.sp,
                            fontWeight    = FontWeight.SemiBold,
                            letterSpacing = 0.4.sp
                        )
                    }

                    Spacer(Modifier.height(36.dp))

                    // ── Info cards ───────────────────────────────────────────
                    ProfileInfoRow(icon = "⬡", label = s.profileSecurity,    value = s.profileSecurityValue)
                    Spacer(Modifier.height(10.dp))
                    ProfileInfoRow(icon = "◷", label = s.profileMemberSince, value = s.profileMemberSinceValue)
                    Spacer(Modifier.height(10.dp))
                    ProfileInfoRow(icon = "⊙", label = s.profileSession,     value = s.profileSessionValue)

                    Spacer(Modifier.height(20.dp))

                    // ── Language toggle ──────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(c.surface)
                            .drawBehind {
                                drawRoundRect(c.glassBorder, cornerRadius = CornerRadius(14.dp.toPx()), style = Stroke(1f))
                                drawLine(
                                    color       = c.purpleGlow.copy(alpha = 0.5f),
                                    start       = Offset(0f, 14.dp.toPx()),
                                    end         = Offset(0f, size.height - 14.dp.toPx()),
                                    strokeWidth = 3.dp.toPx(),
                                    cap         = StrokeCap.Round
                                )
                            }
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🌐", fontSize = 18.sp, color = c.purpleNeon)
                            Spacer(Modifier.width(14.dp))
                            Text(
                                s.profileLanguageLabel,
                                color    = c.dimText,
                                fontSize = 11.sp,
                                letterSpacing = 0.4.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                LanguageChip(
                                    label      = "EN",
                                    isSelected = currentLanguage == AppLanguage.ENGLISH,
                                    onClick    = { onLanguageChange(AppLanguage.ENGLISH) }
                                )
                                LanguageChip(
                                    label      = "RO",
                                    isSelected = currentLanguage == AppLanguage.ROMANIAN,
                                    onClick    = { onLanguageChange(AppLanguage.ROMANIAN) }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(28.dp))

                    // ── Logout button ────────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(c.crimsonGlow.copy(alpha = 0.10f))
                            .drawBehind {
                                drawRoundRect(
                                    color        = c.crimsonGlow.copy(alpha = 0.45f),
                                    cornerRadius = CornerRadius(16.dp.toPx()),
                                    style        = Stroke(1.5f)
                                )
                            }
                            .clickable(remember { MutableInteractionSource() }, null) { onLogout() }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment      = Alignment.CenterVertically,
                            horizontalArrangement  = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("→", fontSize = 16.sp, color = c.crimsonGlow)
                            Text(
                                s.profileLogout,
                                color      = c.crimsonGlow,
                                fontSize   = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val c = appColors
    val bg by animateColorAsState(
        if (isSelected) c.purpleCore else c.glassWhite, tween(200), label = "lang_bg"
    )
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .drawBehind {
                if (!isSelected) drawRoundRect(c.glassBorder, cornerRadius = CornerRadius(8.dp.toPx()), style = Stroke(1f))
            }
            .clickable(remember { MutableInteractionSource() }, null) { onClick() }
            .padding(horizontal = 12.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            color      = if (isSelected) Color.White else c.dimText,
            fontSize   = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun ProfileInfoRow(icon: String, label: String, value: String) {
    val c = appColors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(c.surface)
            .drawBehind {
                drawRoundRect(c.glassBorder, cornerRadius = CornerRadius(14.dp.toPx()), style = Stroke(1f))
                drawLine(
                    color       = c.purpleGlow.copy(alpha = 0.5f),
                    start       = Offset(0f, 14.dp.toPx()),
                    end         = Offset(0f, size.height - 14.dp.toPx()),
                    strokeWidth = 3.dp.toPx(),
                    cap         = StrokeCap.Round
                )
            }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 18.sp, color = c.purpleNeon)
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, color = c.dimText, fontSize = 11.sp, letterSpacing = 0.4.sp)
                Spacer(Modifier.height(2.dp))
                Text(value, color = c.silverText, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}
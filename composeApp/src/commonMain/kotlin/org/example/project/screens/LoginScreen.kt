package org.example.project.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// ─── Hardcoded credentials ────────────────────────────────────────────────────
private const val VALID_EMAIL    = "admin@zkp.ro"
private const val VALID_PASSWORD = "password123"

// ─── Palette (shared with HistoryScreen) ─────────────────────────────────────
private val Void         = Color(0xFF060608)
private val NightSurface = Color(0xFF12121C)
private val GlassBorder  = Color(0x18FFFFFF)
private val PurpleCore   = Color(0xFF7C3AED)
private val PurpleGlow   = Color(0xFF9F67FF)
private val PurpleNeon   = Color(0xFFBF9FFF)
private val PurpleDim    = Color(0xFF2D1B5E)
private val CrimsonGlow  = Color(0xFFE53935)
private val GoldShine    = Color(0xFFFFBD2E)
private val SilverText   = Color(0xFFE2E2F0)
private val DimText      = Color(0xFF6B6B8A)
private val GhostText    = Color(0xFF35354A)

// ─── Screen ───────────────────────────────────────────────────────────────────
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {}    // → navigates to ScanScreen from Launchers.kt
) {
    var email          by remember { mutableStateOf("") }
    var password       by remember { mutableStateOf("") }
    var passwordHidden by remember { mutableStateOf(true) }
    var emailError     by remember { mutableStateOf(false) }
    var passError      by remember { mutableStateOf(false) }
    var showSuccess    by remember { mutableStateOf(false) }

    val passwordFocus = remember { FocusRequester() }
    val focusManager  = LocalFocusManager.current

    val infiniteTransition = rememberInfiniteTransition(label = "ambient_login")
    val ambientAlpha by infiniteTransition.animateFloat(
        initialValue = 0.04f, targetValue = 0.11f,
        animationSpec = infiniteRepeatable(tween(4000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "ambient_alpha"
    )

    fun attemptLogin() {
        focusManager.clearFocus()
        emailError = email.trim() != VALID_EMAIL
        passError  = password     != VALID_PASSWORD
        if (!emailError && !passError) {
            showSuccess = true
        }
    }

    // Navigate after success animation
    LaunchedEffect(showSuccess) {
        if (showSuccess) {
            delay(1400)
            onLoginSuccess()
        }
    }
    Box(modifier = Modifier.fillMaxSize().background(Void)) {

        // ── Ambient glow blobs ───────────────────────────────────────────
        Box(
            modifier = Modifier.size(300.dp).offset(x = (-80).dp, y = 30.dp)
                .blur(120.dp)
                .background(PurpleCore.copy(alpha = ambientAlpha), CircleShape)
        )
        Box(
            modifier = Modifier.size(200.dp).align(Alignment.BottomEnd)
                .offset(x = 60.dp, y = (-100).dp)
                .blur(100.dp)
                .background(Color(0xFF1A0A4E).copy(alpha = ambientAlpha * 1.6f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center
        ) {

            // ── Logo / header bars ───────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Box(Modifier.size(width = 22.dp, height = 2.dp)
                    .background(PurpleGlow, RoundedCornerShape(1.dp)))
                Box(Modifier.size(width = 15.dp, height = 2.dp)
                    .background(PurpleNeon.copy(alpha = 0.5f), RoundedCornerShape(1.dp)))
            }

            Spacer(Modifier.height(24.dp))

            // ── Hero text ────────────────────────────────────────────────
            HeroSection(line1 = "secure", line2 = "your keys", subtitle = "zero-knowledge vault · end-to-end encrypted")

            Spacer(Modifier.height(36.dp))

            // ── Email field ──────────────────────────────────────────────
            ZkpTextField(
                value          = email,
                onValueChange  = { email = it; emailError = false },
                label          = "Email",
                hint           = "your@email.com",
                isError        = emailError,
                errorMessage   = "Invalid email address",
                leadingIcon    = "✉",
                keyboardType   = KeyboardType.Email,
                imeAction      = ImeAction.Next,
                onImeAction    = { passwordFocus.requestFocus() }
            )

            Spacer(Modifier.height(14.dp))

            // ── Password field ───────────────────────────────────────────
            ZkpTextField(
                value          = password,
                onValueChange  = { password = it; passError = false },
                label          = "Password",
                hint           = "••••••••",
                isError        = passError,
                errorMessage   = "Incorrect password",
                leadingIcon    = "⬡",
                keyboardType   = KeyboardType.Password,
                imeAction      = ImeAction.Done,
                onImeAction    = { attemptLogin() },
                isPassword     = true,
                passwordHidden = passwordHidden,
                onTogglePassword = { passwordHidden = !passwordHidden },
                focusRequester = passwordFocus
            )

            Spacer(Modifier.height(6.dp))

            // ── Forgot password ──────────────────────────────────────────
            Text(
                text = "Forgot password?",
                color = PurpleNeon,
                fontSize = 11.sp,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable(remember { MutableInteractionSource() }, null) {}
            )

            Spacer(Modifier.height(28.dp))

            // ── Sign in button ───────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(PurpleCore, PurpleGlow)))
                    .clickable(remember { MutableInteractionSource() }, null) { attemptLogin() }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = showSuccess,
                    transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                    label = "btn_content"
                ) { success ->
                    if (success) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(18.dp).clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✓", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("Access granted", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        Text(
                            text = "Sign in",
                            color = Color.White, fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Divider ──────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f).height(1.dp).background(GhostText))
                Text(
                    text = "OR CONTINUE WITH",
                    color = GhostText, fontSize = 10.sp,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Box(Modifier.weight(1f).height(1.dp).background(GhostText))
            }

            Spacer(Modifier.height(16.dp))

            // ── Biometric button ─────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NightSurface)
                    .drawBehind {
                        drawRoundRect(GlassBorder, cornerRadius = CornerRadius(16.dp.toPx()), style = Stroke(1f))
                    }
                    .clickable(remember { MutableInteractionSource() }, null) {}
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Fingerprint icon (CSS circles approximation)
                    BiometricIcon()
                    Text("Biometric login", color = DimText, fontSize = 13.sp)
                }
            }
        }

        // ── Bottom nav (same as HistoryScreen) ───────────────────────────
        // Removed BottomNavBar from LoginScreen
    }
}

// ─── Hero Section ─────────────────────────────────────────────────────────────
@Composable
private fun HeroSection(line1: String, line2: String, subtitle: String) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(80); visible = true }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(700)) + slideInVertically(tween(700, easing = EaseOutQuart)) { -30 }
    ) {
        Column {
            Text(
                text  = line1,
                style = TextStyle(
                    brush = Brush.linearGradient(listOf(Color.White, Color(0xFFCCBBFF))),
                    fontSize = 34.sp, fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic, lineHeight = 40.sp
                )
            )
            Text(
                text  = line2,
                style = TextStyle(
                    brush = Brush.linearGradient(listOf(PurpleGlow, PurpleNeon)),
                    fontSize = 34.sp, fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic, lineHeight = 40.sp
                )
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(width = 18.dp, height = 1.dp)
                        .background(Brush.horizontalGradient(listOf(Color.Transparent, GoldShine)))
                )
                Spacer(Modifier.width(8.dp))
                Text(subtitle, color = GoldShine, fontSize = 11.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.4.sp)
            }
        }
    }
}

// ─── Text Field ───────────────────────────────────────────────────────────────
@Composable
private fun ZkpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    hint: String,
    isError: Boolean,
    errorMessage: String,
    leadingIcon: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {},
    isPassword: Boolean = false,
    passwordHidden: Boolean = true,
    onTogglePassword: () -> Unit = {},
    focusRequester: FocusRequester? = null
) {
    val borderColor by animateColorAsState(
        if (isError) CrimsonGlow.copy(alpha = 0.7f) else PurpleCore.copy(alpha = 0.5f),
        animationSpec = tween(220), label = "border"
    )
    val accentLineColor by animateColorAsState(
        if (isError) CrimsonGlow else PurpleCore,
        animationSpec = tween(220), label = "accent"
    )

    Column {
        Text(label, color = DimText, fontSize = 10.sp, letterSpacing = 0.8.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(NightSurface)
                .drawBehind {
                    // Full border
                    drawRoundRect(borderColor.copy(alpha = 0.25f), cornerRadius = CornerRadius(14.dp.toPx()), style = Stroke(1f))
                    // Left accent line
                    drawLine(
                        color = accentLineColor.copy(alpha = if (isError) 0.9f else 0.6f),
                        start = Offset(0f, 16.dp.toPx()),
                        end   = Offset(0f, size.height - 16.dp.toPx()),
                        strokeWidth = 3.dp.toPx(),
                        cap   = StrokeCap.Round
                    )
                }
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(leadingIcon, color = DimText, fontSize = 16.sp)
            Spacer(Modifier.width(10.dp))
            BasicTextField_ZKP(
                value          = value,
                onValueChange  = onValueChange,
                hint           = hint,
                keyboardType   = keyboardType,
                imeAction      = imeAction,
                onImeAction    = onImeAction,
                isPassword     = isPassword,
                passwordHidden = passwordHidden,
                focusRequester = focusRequester,
                modifier       = Modifier.weight(1f)
            )
            if (isPassword) {
                Spacer(Modifier.width(6.dp))
                Text(
                    text = if (passwordHidden) "○" else "●",
                    color = DimText, fontSize = 14.sp,
                    modifier = Modifier.clickable(remember { MutableInteractionSource() }, null) { onTogglePassword() }
                )
            }
        }
        AnimatedVisibility(visible = isError, enter = fadeIn(tween(200)) + expandVertically(tween(200))) {
            Text(errorMessage, color = CrimsonGlow, fontSize = 10.sp, modifier = Modifier.padding(top = 5.dp, start = 4.dp))
        }
    }
}

// ─── Inner BasicTextField wrapper ────────────────────────────────────────────
@Composable
private fun BasicTextField_ZKP(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    onImeAction: () -> Unit,
    isPassword: Boolean,
    passwordHidden: Boolean,
    focusRequester: FocusRequester?,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.text.BasicTextField(
        value           = value,
        onValueChange   = onValueChange,
        singleLine      = true,
        textStyle       = TextStyle(color = SilverText, fontSize = 13.sp),
        visualTransformation = if (isPassword && passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onNext = { onImeAction() },
            onDone = { onImeAction() }
        ),
        modifier = modifier
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier),
        decorationBox = { inner ->
            Box {
                if (value.isEmpty()) Text(hint, color = GhostText, fontSize = 13.sp)
                inner()
            }
        }
    )
}

// ─── Biometric icon ───────────────────────────────────────────────────────────
@Composable
private fun BiometricIcon() {
    // Simple concentric-arc fingerprint approximation using nested rounded boxes
    Box(
        modifier = Modifier.size(22.dp)
            .clip(CircleShape)
            .background(PurpleDim)
            .drawBehind {
                drawCircle(PurpleCore.copy(alpha = 0.4f), radius = size.minDimension / 2, style = Stroke(1.dp.toPx()))
            },
        contentAlignment = Alignment.Center
    ) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(PurpleGlow.copy(alpha = 0.3f))
            .drawBehind { drawCircle(PurpleGlow.copy(alpha = 0.6f), style = Stroke(1.dp.toPx())) })
    }
}

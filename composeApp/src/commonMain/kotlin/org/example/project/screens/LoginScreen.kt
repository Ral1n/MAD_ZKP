package org.example.project.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import kotlin.random.Random

private const val VALID_EMAIL    = "admin@zkp.ro"
private const val VALID_PASSWORD = "password123"

private val Void        = Color(0xFF060608)
private val NightSurf   = Color(0xFF0D0D16)
private val CardBg      = Color(0xE80E0E18)
private val GlassBorder = Color(0x10FFFFFF)
private val PurpleCore  = Color(0xFF7C3AED)
private val PurpleGlow  = Color(0xFF9F67FF)
private val PurpleNeon  = Color(0xFFBF9FFF)
private val PurpleDim   = Color(0xFF2D1B5E)
private val CrimsonGlow = Color(0xFFE53935)
private val GoldShine   = Color(0xFFFFBD2E)
private val SilverText  = Color(0xFFE2E2F0)
private val DimText     = Color(0xFF6B6B8A)
private val GhostText   = Color(0xFF22223A)
private val EmeraldGlow = Color(0xFF10B981)

private data class Drop(
    var x: Float, var y: Float,
    val len: Float, val spd: Float,
    val alpha: Float, val sw: Float, val ci: Int
)
private val rainPal = listOf(
    Color(0xFF7C3AED), Color(0xFF9F67FF),
    Color(0xFFBF9FFF), Color(0xFF6428C8), Color(0xFF5014B4)
)

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit = {}) {

    var email          by remember { mutableStateOf("") }
    var password       by remember { mutableStateOf("") }
    var passwordHidden by remember { mutableStateOf(true) }
    var emailError     by remember { mutableStateOf(false) }
    var passError      by remember { mutableStateOf(false) }
    var showSuccess    by remember { mutableStateOf(false) }
    val passFocus      = remember { FocusRequester() }
    val focusMgr       = LocalFocusManager.current

    var cW by remember { mutableStateOf(0f) }
    var cH by remember { mutableStateOf(0f) }
    val drops  = remember { mutableStateListOf<Drop>() }
    val splX   = remember { mutableStateListOf<Float>() }
    val splY   = remember { mutableStateListOf<Float>() }
    val splC   = remember { mutableStateListOf<Color>() }
    val splP   = remember { mutableStateListOf<Float>() }

    LaunchedEffect(cW, cH) {
        if (cW > 0f && cH > 0f && drops.isEmpty()) {
            repeat(120) {
                drops.add(Drop(
                    Random.nextFloat()*cW, Random.nextFloat()*cH,
                    10f+Random.nextFloat()*24f, 2.5f+Random.nextFloat()*5f,
                    0.12f+Random.nextFloat()*0.45f, 0.8f+Random.nextFloat()*1.3f,
                    Random.nextInt(rainPal.size)
                ))
            }
        }
    }

    LaunchedEffect(cW, cH) {
        while (true) {
            delay(16L)
            if (cW > 0f && cH > 0f) {
                val it = drops.listIterator()
                while (it.hasNext()) {
                    val d = it.next()
                    if (d.y + d.spd > cH + 12f) {
                        if (Random.nextFloat() < 0.22f) {
                            splX.add(d.x); splY.add(cH-2f); splC.add(rainPal[d.ci]); splP.add(0f)
                        }
                        it.set(Drop(Random.nextFloat()*cW, -26f, 10f+Random.nextFloat()*24f,
                            2.5f+Random.nextFloat()*5f, 0.12f+Random.nextFloat()*0.45f,
                            0.8f+Random.nextFloat()*1.3f, Random.nextInt(rainPal.size)))
                    } else it.set(d.copy(y = d.y + d.spd))
                }
                val pi = splP.listIterator()
                val xi = splX.listIterator(); val yi = splY.listIterator(); val ci = splC.listIterator()
                while (pi.hasNext()) {
                    xi.next(); yi.next(); ci.next()
                    val p = pi.next() + 0.06f
                    if (p >= 1f) { pi.remove(); xi.remove(); yi.remove(); ci.remove() } else pi.set(p)
                }
            }
        }
    }

    val inf = rememberInfiniteTransition(label = "bg")
    val blobA by inf.animateFloat(0.04f, 0.13f,
        infiniteRepeatable(tween(5000, easing=EaseInOutSine), RepeatMode.Reverse), label="blob")
    val scanF by inf.animateFloat(0f, 1f,
        infiniteRepeatable(tween(4000, easing=LinearEasing), RepeatMode.Restart), label="scan")

    fun tryLogin() {
        focusMgr.clearFocus()
        emailError = email.trim() != VALID_EMAIL
        passError  = password != VALID_PASSWORD
        if (!emailError && !passError) showSuccess = true
    }

    LaunchedEffect(showSuccess) { if (showSuccess) { delay(1500); onLoginSuccess() } }

    Box(Modifier.fillMaxSize().background(Void)) {

        // ── Rain + grid canvas ────────────────────────────────────────────────
        Canvas(Modifier.fillMaxSize()) {
            cW = size.width; cH = size.height
            val gs = 40.dp.toPx()
            val gc = Color(0xFF7C3AED).copy(alpha=0.035f)
            var gx = 0f; while (gx<=size.width)  { drawLine(gc, Offset(gx,0f), Offset(gx,size.height)); gx+=gs }
            var gy = 0f; while (gy<=size.height) { drawLine(gc, Offset(0f,gy), Offset(size.width,gy)); gy+=gs }

            drops.forEach { d ->
                val base = rainPal[d.ci]
                drawLine(
                    brush = Brush.verticalGradient(
                        listOf(base.copy(alpha=0f), base.copy(alpha=d.alpha*0.55f), base.copy(alpha=d.alpha)),
                        startY=d.y, endY=d.y+d.len
                    ),
                    start=Offset(d.x,d.y), end=Offset(d.x,d.y+d.len),
                    strokeWidth=d.sw, cap=StrokeCap.Round
                )
                drawCircle(base.copy(alpha=d.alpha*0.9f), d.sw*1.1f, Offset(d.x,d.y+d.len))
            }
            splP.forEachIndexed { i, p ->
                if (i < splX.size) drawCircle(splC[i].copy(alpha=(1f-p)*0.7f),
                    p*18.dp.toPx(), Offset(splX[i],splY[i]), style=Stroke(1.5f))
            }
            val sy = scanF * size.height
            val sa = when { scanF<0.05f->scanF/0.05f*0.5f; scanF>0.95f->(1f-scanF)/0.05f*0.5f; else->0.4f }
            drawLine(
                Brush.horizontalGradient(listOf(Color.Transparent, PurpleGlow.copy(alpha=sa), Color.Transparent)),
                Offset(0f,sy), Offset(size.width,sy), 1f
            )
        }

        // Blobs
        Box(Modifier.size(360.dp).offset((-90).dp,40.dp).blur(140.dp).background(PurpleCore.copy(alpha=blobA),CircleShape))
        Box(Modifier.size(240.dp).align(Alignment.BottomEnd).offset(70.dp,(-80).dp).blur(120.dp).background(Color(0xFF1A0A4E).copy(alpha=blobA*1.7f),CircleShape))
        Box(Modifier.size(180.dp).align(Alignment.CenterEnd).offset(30.dp).blur(100.dp).background(PurpleGlow.copy(alpha=blobA*0.6f),CircleShape))

        // Node dots
        Column(Modifier.padding(18.dp), verticalArrangement=Arrangement.spacedBy(4.dp)) {
            PulseDot(PurpleCore, 0); PulseDot(GoldShine, 400); PulseDot(EmeraldGlow, 800)
        }

        // Corner tag
        Text("ZKP-VAULT", Modifier.align(Alignment.TopEnd).padding(top=20.dp, end=20.dp),
            color=PurpleCore.copy(alpha=0.5f), fontSize=9.sp, fontWeight=FontWeight.Bold, letterSpacing=1.5.sp)

        // ── Centered card — max 420dp, works on any screen size ──────────────
        Box(Modifier.fillMaxSize().systemBarsPadding(), contentAlignment=Alignment.Center) {
            Box(
                modifier = Modifier
                    .widthIn(max = 420.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(CardBg)
                    .drawBehind {
                        drawRoundRect(GlassBorder, cornerRadius=CornerRadius(28.dp.toPx()), style=Stroke(1f))
                        drawRoundRect(PurpleCore.copy(alpha=0.10f), cornerRadius=CornerRadius(28.dp.toPx()), style=Stroke(1.5f))
                        drawLine(
                            Brush.horizontalGradient(listOf(Color.Transparent,PurpleCore,PurpleGlow,PurpleCore,Color.Transparent)),
                            Offset(size.width*0.1f, size.height-1f), Offset(size.width*0.9f, size.height-1f), 2f
                        )
                        drawLine(
                            Brush.horizontalGradient(listOf(Color.Transparent,PurpleGlow.copy(alpha=0.4f),Color.Transparent)),
                            Offset(size.width*0.2f,0f), Offset(size.width*0.8f,0f), 1f
                        )
                    }
                    .padding(horizontal=32.dp, vertical=36.dp)
            ) {
                CardContent(
                    email=email, onEmailChange={ email=it; emailError=false },
                    password=password, onPassChange={ password=it; passError=false },
                    passwordHidden=passwordHidden, onTogglePass={ passwordHidden=!passwordHidden },
                    emailError=emailError, passError=passError, showSuccess=showSuccess,
                    passFocus=passFocus, onLogin={ tryLogin() }
                )
            }
        }

        // Bottom shimmer
        Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(2.dp)
            .background(Brush.horizontalGradient(listOf(Color.Transparent,PurpleCore,PurpleGlow,PurpleCore,Color.Transparent))))
    }
}

@Composable
private fun CardContent(
    email: String, onEmailChange: (String)->Unit,
    password: String, onPassChange: (String)->Unit,
    passwordHidden: Boolean, onTogglePass: ()->Unit,
    emailError: Boolean, passError: Boolean, showSuccess: Boolean,
    passFocus: FocusRequester, onLogin: ()->Unit
) {
    var vis by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(80); vis=true }

    Column {
        AnimatedVisibility(vis, enter=fadeIn(tween(600))+slideInVertically(tween(600,easing=EaseOutQuart)){-20}) {
            Column(verticalArrangement=Arrangement.spacedBy(5.dp)) {
                Box(Modifier.size(width=24.dp,height=2.5.dp).background(PurpleGlow,RoundedCornerShape(2.dp)))
                Box(Modifier.size(width=16.dp,height=2.5.dp).background(PurpleNeon.copy(alpha=0.45f),RoundedCornerShape(2.dp)))
            }
        }
        Spacer(Modifier.height(26.dp))

        AnimatedVisibility(vis, enter=fadeIn(tween(700,80))+slideInVertically(tween(700,80,EaseOutQuart)){-28}) {
            Column {
                Text("secure", style=TextStyle(
                    brush=Brush.linearGradient(listOf(Color.White,Color(0xFFCCBBFF))),
                    fontSize=38.sp, fontWeight=FontWeight.Bold, fontStyle=FontStyle.Italic, lineHeight=42.sp))
                Text("your keys", style=TextStyle(
                    brush=Brush.linearGradient(listOf(PurpleGlow,PurpleNeon)),
                    fontSize=38.sp, fontWeight=FontWeight.ExtraBold, fontStyle=FontStyle.Italic, lineHeight=42.sp))
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment=Alignment.CenterVertically) {
                    Box(Modifier.size(width=20.dp,height=1.dp)
                        .background(Brush.horizontalGradient(listOf(Color.Transparent,GoldShine))))
                    Spacer(Modifier.width(8.dp))
                    Text("zero-knowledge vault · e2e encrypted",
                        color=GoldShine, fontSize=10.sp, fontWeight=FontWeight.Medium, letterSpacing=0.6.sp)
                }
            }
        }
        Spacer(Modifier.height(30.dp))

        AnimatedVisibility(vis, enter=fadeIn(tween(600,160))+slideInVertically(tween(600,160,EaseOutQuart)){16}) {
            LoginField(email, onEmailChange, "EMAIL", "your@email.com", emailError,
                "Invalid email address", "✉", KeyboardType.Email, ImeAction.Next,
                onImeAction={ passFocus.requestFocus() })
        }
        Spacer(Modifier.height(16.dp))

        AnimatedVisibility(vis, enter=fadeIn(tween(600,220))+slideInVertically(tween(600,220,EaseOutQuart)){16}) {
            LoginField(password, onPassChange, "PASSWORD", "••••••••", passError,
                "Incorrect password", "⬡", KeyboardType.Password, ImeAction.Done,
                onImeAction=onLogin, isPassword=true, passwordHidden=passwordHidden,
                onTogglePassword=onTogglePass, focusRequester=passFocus)
        }
        Spacer(Modifier.height(8.dp))

        AnimatedVisibility(vis, enter=fadeIn(tween(500,280))) {
            Text("Forgot password?", color=PurpleCore, fontSize=11.sp, letterSpacing=0.3.sp,
                modifier=Modifier.align(Alignment.End).clickable(remember{MutableInteractionSource()},null){})
        }
        Spacer(Modifier.height(28.dp))

        AnimatedVisibility(vis, enter=fadeIn(tween(600,320))+slideInVertically(tween(600,320,EaseOutQuart)){16}) {
            Box(
                modifier=Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .background(if(showSuccess) Brush.linearGradient(listOf(Color(0xFF059669),EmeraldGlow))
                    else Brush.linearGradient(listOf(PurpleCore,PurpleGlow)))
                    .clickable(remember{MutableInteractionSource()},null){ onLogin() }
                    .padding(vertical=16.dp),
                contentAlignment=Alignment.Center
            ) {
                AnimatedContent(showSuccess, transitionSpec={fadeIn(tween(300)) togetherWith fadeOut(tween(200))}, label="btn") { s ->
                    if (s) Row(verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.size(18.dp).clip(CircleShape).background(Color.White.copy(alpha=0.2f)), contentAlignment=Alignment.Center) {
                            Text("✓", color=Color.White, fontSize=11.sp, fontWeight=FontWeight.Bold)
                        }
                        Text("Access granted", color=Color.White, fontSize=14.sp, fontWeight=FontWeight.SemiBold)
                    } else Text("Sign in", color=Color.White, fontSize=14.sp, fontWeight=FontWeight.Bold, letterSpacing=0.6.sp)
                }
            }
        }
        Spacer(Modifier.height(22.dp))

        AnimatedVisibility(vis, enter=fadeIn(tween(500,380))) {
            Row(verticalAlignment=Alignment.CenterVertically) {
                Box(Modifier.weight(1f).height(1.dp).background(GhostText))
                Text("OR CONTINUE WITH", color=DimText.copy(alpha=0.4f), fontSize=9.sp,
                    letterSpacing=1.sp, modifier=Modifier.padding(horizontal=12.dp))
                Box(Modifier.weight(1f).height(1.dp).background(GhostText))
            }
        }
        Spacer(Modifier.height(14.dp))

        AnimatedVisibility(vis, enter=fadeIn(tween(600,440))+slideInVertically(tween(600,440,EaseOutQuart)){16}) {
            Box(
                modifier=Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(NightSurf)
                    .drawBehind { drawRoundRect(GlassBorder, cornerRadius=CornerRadius(14.dp.toPx()), style=Stroke(1f)) }
                    .clickable(remember{MutableInteractionSource()},null){}
                    .padding(vertical=13.dp),
                contentAlignment=Alignment.Center
            ) {
                Row(verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                    BioIcon(); Text("Biometric login", color=DimText, fontSize=13.sp)
                }
            }
        }
    }
}

@Composable
private fun LoginField(
    value: String, onValueChange: (String)->Unit,
    label: String, hint: String, isError: Boolean, errorMessage: String,
    leadingIcon: String, keyboardType: KeyboardType=KeyboardType.Text,
    imeAction: ImeAction=ImeAction.Done, onImeAction: ()->Unit={},
    isPassword: Boolean=false, passwordHidden: Boolean=true,
    onTogglePassword: ()->Unit={}, focusRequester: FocusRequester?=null
) {
    val borderC by animateColorAsState(if(isError) CrimsonGlow.copy(alpha=0.5f) else PurpleCore.copy(alpha=0.22f), tween(220), label="b")
    val accentC by animateColorAsState(if(isError) CrimsonGlow else PurpleCore, tween(220), label="a")

    Column {
        Text(label, color=DimText, fontSize=9.sp, letterSpacing=1.2.sp, fontWeight=FontWeight.Bold)
        Spacer(Modifier.height(7.dp))
        Row(
            modifier=Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(NightSurf)
                .drawBehind {
                    drawRoundRect(borderC, cornerRadius=CornerRadius(14.dp.toPx()), style=Stroke(1f))
                    drawLine(accentC.copy(alpha=if(isError) 0.9f else 0.7f),
                        Offset(0f,14.dp.toPx()), Offset(0f,size.height-14.dp.toPx()),
                        strokeWidth=3.dp.toPx(), cap=StrokeCap.Round)
                }
                .padding(horizontal=14.dp, vertical=13.dp),
            verticalAlignment=Alignment.CenterVertically
        ) {
            Text(leadingIcon, color=DimText, fontSize=15.sp)
            Spacer(Modifier.width(10.dp))
            androidx.compose.foundation.text.BasicTextField(
                value=value, onValueChange=onValueChange, singleLine=true,
                textStyle=TextStyle(color=SilverText, fontSize=13.sp),
                visualTransformation=if(isPassword&&passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions=KeyboardOptions(keyboardType=keyboardType, imeAction=imeAction),
                keyboardActions=KeyboardActions(onNext={onImeAction()}, onDone={onImeAction()}),
                modifier=Modifier.weight(1f).then(if(focusRequester!=null) Modifier.focusRequester(focusRequester) else Modifier),
                decorationBox={ inner -> Box { if(value.isEmpty()) Text(hint, color=GhostText, fontSize=13.sp); inner() } }
            )
            if (isPassword) {
                Spacer(Modifier.width(6.dp))
                Text(if(passwordHidden) "○" else "●" , color=DimText, fontSize=14.sp,
                    modifier=Modifier.clickable(remember{MutableInteractionSource()},null){ onTogglePassword() })
            }
        }
        AnimatedVisibility(isError, enter=fadeIn(tween(200))+expandVertically(tween(200))) {
            Text(errorMessage, color=CrimsonGlow, fontSize=10.sp, modifier=Modifier.padding(top=5.dp, start=4.dp))
        }
    }
}

@Composable
private fun PulseDot(color: Color, delayMs: Int) {
    val inf = rememberInfiniteTransition(label="d$delayMs")
    val a by inf.animateFloat(0.3f,1f, infiniteRepeatable(tween(2000,easing=EaseInOutSine,delayMillis=delayMs),RepeatMode.Reverse), label="a")
    val s by inf.animateFloat(1f,1.35f, infiniteRepeatable(tween(2000,easing=EaseInOutSine,delayMillis=delayMs),RepeatMode.Reverse), label="s")
    Box(Modifier.size((5f*s).dp).clip(CircleShape).background(color.copy(alpha=a)))
}

@Composable
private fun BioIcon() {
    Box(Modifier.size(22.dp).clip(CircleShape).background(PurpleDim)
        .drawBehind { drawCircle(PurpleCore.copy(alpha=0.4f), size.minDimension/2, style=Stroke(1.dp.toPx())) },
        contentAlignment=Alignment.Center) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(PurpleGlow.copy(alpha=0.3f))
            .drawBehind { drawCircle(PurpleGlow.copy(alpha=0.6f), style=Stroke(1.dp.toPx())) })
    }
}
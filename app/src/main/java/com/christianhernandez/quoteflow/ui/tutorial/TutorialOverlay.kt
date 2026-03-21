package com.christianhernandez.quoteflow.ui.tutorial

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwipeRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// Direction colors matching FeedScreen
private val WisdomBlue = Color(0xFF3B82F6)
private val DisciplineGreen = Color(0xFF10B981)
private val ReflectionOrange = Color(0xFFF97316)
private val PhilosophyPurple = Color(0xFF8B5CF6)
private val IndigoPrimary = Color(0xFF3F51B5)

@Composable
fun TutorialOverlay(
    viewModel: TutorialViewModel,
    language: String,
) {
    val showTutorial by viewModel.showTutorial.collectAsState()
    val currentStep by viewModel.currentStep.collectAsState()

    if (!showTutorial) return

    val isEs = language == "es"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.88f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Step content with slide animation
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(400, easing = FastOutSlowInEasing),
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = tween(400, easing = FastOutSlowInEasing),
                    )
                },
                label = "tutorial_step",
                modifier = Modifier.weight(1f),
            ) { step ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    when (step) {
                        0 -> WelcomeStep(isEs)
                        1 -> CardsStep(isEs)
                        2 -> DirectionsStep(isEs)
                        3 -> DoubleTapStep(isEs)
                        4 -> SaveVaultStep(isEs)
                        5 -> ShareStep(isEs)
                        6 -> PhilosophyMapStep(isEs)
                        7 -> DailyChallengeStep(isEs)
                        8 -> AmbientMusicStep(isEs)
                        9 -> ReadyStep(isEs)
                    }
                }
            }

            // Step indicator dots
            StepIndicator(
                currentStep = currentStep,
                totalSteps = TutorialViewModel.TOTAL_STEPS,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action button
            Button(
                onClick = { viewModel.nextStep() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = IndigoPrimary,
                    contentColor = Color.White,
                ),
            ) {
                Text(
                    text = getButtonText(currentStep, isEs),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun StepIndicator(currentStep: Int, totalSteps: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
    ) {
        repeat(totalSteps) { index ->
            val isActive = index == currentStep
            val isPast = index < currentStep

            val scale by animateFloatAsState(
                targetValue = if (isActive) 1.3f else 1f,
                animationSpec = tween(300),
                label = "dot_scale_$index",
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .scale(scale)
                    .size(if (isActive) 10.dp else 8.dp)
                    .background(
                        color = when {
                            isActive -> IndigoPrimary
                            isPast -> IndigoPrimary.copy(alpha = 0.5f)
                            else -> Color.White.copy(alpha = 0.3f)
                        },
                        shape = CircleShape,
                    ),
            )
        }
    }
}

private fun getButtonText(step: Int, isEs: Boolean): String = when (step) {
    0 -> if (isEs) "Comenzar tutorial" else "Start tutorial"
    2 -> if (isEs) "Entendido" else "Got it"
    9 -> if (isEs) "Comenzar" else "Let's go"
    else -> if (isEs) "Siguiente" else "Next"
}

// -- Step Composables --

@Composable
private fun TutorialCard(
    icon: ImageVector,
    iconColor: Color = IndigoPrimary,
    title: String,
    description: String,
    extraContent: (@Composable () -> Unit)? = null,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A2E),
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = iconColor,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = description,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = Color.White.copy(alpha = 0.75f),
                textAlign = TextAlign.Center,
            )
            if (extraContent != null) {
                Spacer(modifier = Modifier.height(20.dp))
                extraContent()
            }
        }
    }
}

@Composable
private fun WelcomeStep(isEs: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "welcome_pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "welcome_pulse_val",
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A2E),
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "QuoteFlow",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = Color.White,
                modifier = Modifier.scale(pulse),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(3.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(WisdomBlue, PhilosophyPurple),
                        ),
                        shape = RoundedCornerShape(2.dp),
                    ),
            )
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = if (isEs)
                    "Tu viaje filosofico comienza aqui"
                else
                    "Your philosophical journey begins here",
                fontSize = 17.sp,
                lineHeight = 24.sp,
                fontStyle = FontStyle.Italic,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun CardsStep(isEs: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "swipe_hand")
    val handOffset by infiniteTransition.animateFloat(
        initialValue = -30f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "hand_offset",
    )

    TutorialCard(
        icon = Icons.Default.AutoStories,
        title = if (isEs) "Las cartas" else "The Cards",
        description = if (isEs)
            "Cada carta contiene una frase de un filosofo. Desliza para explorar."
        else
            "Each card contains a quote from a philosopher. Swipe to explore.",
    ) {
        // Mock card preview
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF252547),
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = if (isEs)
                        "\"Conocete a ti mismo.\""
                    else
                        "\"Know thyself.\"",
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isEs) "— Socrates" else "— Socrates",
                    fontSize = 13.sp,
                    color = IndigoPrimary.copy(alpha = 0.8f),
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        // Animated swipe hand
        Text(
            text = Icons.Default.SwipeRight.name,
            modifier = Modifier.offset(x = handOffset.dp),
        )
        Icon(
            imageVector = Icons.Default.SwipeRight,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .offset(x = handOffset.dp),
            tint = Color.White.copy(alpha = 0.6f),
        )
    }
}

@Composable
private fun DirectionsStep(isEs: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "arrow_pulse")
    val arrowScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "arrow_scale",
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A2E),
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = if (isEs) "4 Direcciones" else "4 Directions",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Direction arrows layout
            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center,
            ) {
                // Up - Wisdom
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .scale(arrowScale),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = "\u2191", fontSize = 32.sp, color = WisdomBlue, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (isEs) "Sabiduria" else "Wisdom",
                        fontSize = 11.sp,
                        color = WisdomBlue,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                // Right - Discipline
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .scale(arrowScale),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = "\u2192", fontSize = 32.sp, color = DisciplineGreen, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (isEs) "Disciplina" else "Discipline",
                        fontSize = 11.sp,
                        color = DisciplineGreen,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                // Left - Reflection
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .scale(arrowScale),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = "\u2190", fontSize = 32.sp, color = ReflectionOrange, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (isEs) "Reflexion" else "Reflection",
                        fontSize = 11.sp,
                        color = ReflectionOrange,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                // Down - Philosophy
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .scale(arrowScale),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = "\u2193", fontSize = 32.sp, color = PhilosophyPurple, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (isEs) "Filosofia" else "Philosophy",
                        fontSize = 11.sp,
                        color = PhilosophyPurple,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isEs)
                    "Cada direccion representa una orientacion filosofica. La direccion que elijas determina tu proxima frase."
                else
                    "Each direction represents a philosophical orientation. Your choice determines the next quote.",
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun DoubleTapStep(isEs: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "heart_pulse")
    val heartScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "heart_scale",
    )

    TutorialCard(
        icon = Icons.Default.Favorite,
        iconColor = Color(0xFFEF4444),
        title = if (isEs) "Doble tap = Me gusta" else "Double tap = Like",
        description = if (isEs)
            "Toca dos veces la carta para dar me gusta. Suma 2 puntos a tu mapa filosofico."
        else
            "Double-tap the card to like it. Adds 2 points to your philosophy map.",
    ) {
        // Animated double-tap gesture indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "tap tap",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.5f),
                fontStyle = FontStyle.Italic,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .scale(heartScale),
                tint = Color(0xFFEF4444),
            )
        }
    }
}

@Composable
private fun SaveVaultStep(isEs: Boolean) {
    TutorialCard(
        icon = Icons.Default.Bookmark,
        iconColor = Color(0xFFFBBF24),
        title = if (isEs) "Guardar en Vault" else "Save to Vault",
        description = if (isEs)
            "Toca el icono de marcador para guardar frases que te inspiren. Suma 3 puntos."
        else
            "Tap the bookmark icon to save quotes that inspire you. Adds 3 points.",
    )
}

@Composable
private fun ShareStep(isEs: Boolean) {
    TutorialCard(
        icon = Icons.Default.Share,
        iconColor = Color(0xFF06B6D4),
        title = if (isEs) "Compartir" else "Share",
        description = if (isEs)
            "Comparte frases con tus amigos. Suma 4 puntos a tu orientacion."
        else
            "Share quotes with your friends. Adds 4 points to your orientation.",
    )
}

@Composable
private fun PhilosophyMapStep(isEs: Boolean) {
    TutorialCard(
        icon = Icons.Default.Radar,
        iconColor = IndigoPrimary,
        title = if (isEs) "Mapa Filosofico" else "Philosophy Map",
        description = if (isEs)
            "Tu mapa filosofico se construye con cada interaccion. Descubre tu orientacion dominante."
        else
            "Your philosophy map is built with every interaction. Discover your dominant orientation.",
    ) {
        // Mini radar chart illustration
        MiniRadarChart()
    }
}

@Composable
private fun DailyChallengeStep(isEs: Boolean) {
    TutorialCard(
        icon = Icons.Default.EmojiEvents,
        iconColor = Color(0xFFF59E0B),
        title = if (isEs) "Retos Diarios" else "Daily Challenges",
        description = if (isEs)
            "Completa retos diarios leyendo 8 frases. Construye tu racha."
        else
            "Complete daily challenges by reading 8 quotes. Build your streak.",
    )
}

@Composable
private fun AmbientMusicStep(isEs: Boolean) {
    TutorialCard(
        icon = Icons.Default.MusicNote,
        iconColor = Color(0xFF8B5CF6),
        title = if (isEs) "Musica Ambient" else "Ambient Music",
        description = if (isEs)
            "Disfruta de musica ambient mientras lees. Toca el icono de musica en la barra superior."
        else
            "Enjoy ambient music while you read. Tap the music icon in the top bar.",
    )
}

@Composable
private fun ReadyStep(isEs: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "celebration")
    val celebrationScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "celebration_scale",
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A2E),
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Sparkle particles
            SparkleEffect()

            Icon(
                imageVector = Icons.Default.Celebration,
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .scale(celebrationScale),
                tint = Color(0xFFFBBF24),
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = if (isEs) "Listo!" else "You're Ready!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isEs)
                    "Ya conoces QuoteFlow. Tu viaje filosofico comienza ahora."
                else
                    "You know QuoteFlow. Your philosophical journey begins now.",
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = Color.White.copy(alpha = 0.75f),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun MiniRadarChart() {
    val infiniteTransition = rememberInfiniteTransition(label = "radar_anim")
    val radarPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "radar_pulse",
    )

    Canvas(modifier = Modifier.size(100.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2 * 0.85f
        val colors = listOf(WisdomBlue, DisciplineGreen, PhilosophyPurple, ReflectionOrange)
        val values = listOf(0.8f, 0.5f, 0.6f, 0.7f)

        // Draw axes
        for (i in 0 until 4) {
            val angle = Math.toRadians((i * 90.0 - 90.0))
            val endX = center.x + radius * cos(angle).toFloat()
            val endY = center.y + radius * sin(angle).toFloat()
            drawLine(
                color = colors[i].copy(alpha = 0.3f),
                start = center,
                end = Offset(endX, endY),
                strokeWidth = 1.5f,
            )
        }

        // Draw filled area
        val points = values.mapIndexed { i, v ->
            val angle = Math.toRadians((i * 90.0 - 90.0))
            val r = radius * v * radarPulse
            Offset(
                center.x + r * cos(angle).toFloat(),
                center.y + r * sin(angle).toFloat(),
            )
        }

        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(points[0].x, points[0].y)
            for (i in 1 until points.size) {
                lineTo(points[i].x, points[i].y)
            }
            close()
        }
        drawPath(path, IndigoPrimary.copy(alpha = 0.25f))
        drawPath(
            path,
            IndigoPrimary.copy(alpha = 0.6f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f),
        )

        // Draw dots at vertices
        points.forEachIndexed { i, point ->
            drawCircle(colors[i], radius = 4f, center = point)
        }
    }
}

@Composable
private fun SparkleEffect() {
    val infiniteTransition = rememberInfiniteTransition(label = "sparkle")

    val sparkles = remember {
        List(8) { Triple(Random.nextFloat() * 200 - 100, Random.nextFloat() * 80 - 40, Random.nextFloat()) }
    }

    Box(modifier = Modifier.size(120.dp, 40.dp)) {
        sparkles.forEachIndexed { index, (x, y, delay) ->
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.1f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 800,
                        delayMillis = (delay * 600).toInt(),
                        easing = FastOutSlowInEasing,
                    ),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "sparkle_alpha_$index",
            )
            Text(
                text = "\u2728",
                modifier = Modifier
                    .offset(x = x.dp, y = y.dp)
                    .alpha(alpha),
                fontSize = 14.sp,
            )
        }
    }
}

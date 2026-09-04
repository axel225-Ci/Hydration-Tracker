package com.example

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkOutlineVariant
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TurquoisePrimary
import com.example.ui.theme.TurquoiseSecondary
import com.example.ui.theme.WaterBlueEnd
import com.example.ui.theme.WaterBlueStart

@Composable
fun CircularProgressGauge(
    uiState: HydrationUiState,
    modifier: Modifier = Modifier,
    size: Dp = 270.dp,
    strokeWidth: Dp = 18.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = uiState.progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "progress_animation"
    )

    // Subtle pulsing scale for water drop icon
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier
            .size(size)
            .testTag("circular_progress_gauge"),
        contentAlignment = Alignment.Center
    ) {
        // Outer Canvas for Track and Gradient Progress Arc
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = strokeWidth.toPx()
            val arcSize = Size(
                width = this.size.width - strokePx,
                height = this.size.height - strokePx
            )
            val topLeft = Offset(strokePx / 2f, strokePx / 2f)

            // Background Track Arc
            drawArc(
                color = DarkOutlineVariant.copy(alpha = 0.6f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Turquoise Gradient Progress Arc
            if (animatedProgress > 0.005f) {
                val brush = Brush.sweepGradient(
                    0.0f to WaterBlueStart,
                    0.5f to TurquoiseSecondary,
                    1.0f to WaterBlueEnd,
                    center = Offset(this.size.width / 2f, this.size.height / 2f)
                )

                drawArc(
                    brush = brush,
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }

        // Inner Content Container
        Column(
            modifier = Modifier
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Droplet / Success Icon
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceVariant.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isGoalReached) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Objectif atteint",
                        tint = TurquoisePrimary,
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = "Goutte d'eau",
                        tint = TurquoisePrimary,
                        modifier = Modifier
                            .size(28.dp)
                            .scale(pulseScale)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Liters Display
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = uiState.currentInLiters,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = (-1).sp
                )
                Text(
                    text = " L",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TurquoisePrimary,
                    modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                )
            }

            // Target Info
            Text(
                text = "${uiState.currentMl} / ${uiState.targetMl} ml",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Percentage Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (uiState.isGoalReached) TurquoisePrimary.copy(alpha = 0.2f) else DarkSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (uiState.isGoalReached) TurquoisePrimary else DarkOutlineVariant
                )
            ) {
                Text(
                    text = if (uiState.isGoalReached) "Objectif 100% ✨" else "${uiState.progressPercent}%",
                    color = if (uiState.isGoalReached) TurquoisePrimary else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun CyberGridBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    // Faint grid lines animation
    val infiniteTransition = rememberInfiniteTransition(label = "faint_grid")
    val gridOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 120f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "grid_offset"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .drawBehind {
                val gridSpacing = 60.dp.toPx()
                val gridColor = Color(0x0600F2FE) // Faint cyber cyan
                val violetGlowColor = Color(0x088A2BE2) // Faint violet ambient grid
                
                // Horizontal lines with movement offset
                var y = (-gridSpacing + (gridOffset % gridSpacing))
                while (y < size.height + gridSpacing) {
                    val lineAlpha = if (y % (gridSpacing * 2) == 0f) gridColor else violetGlowColor
                    drawLine(
                        color = lineAlpha,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1.5f
                    )
                    y += gridSpacing
                }

                // Vertical lines
                var x = (-gridSpacing + (gridOffset % gridSpacing))
                while (x < size.width + gridSpacing) {
                    val lineAlpha = if (x % (gridSpacing * 2) == 0f) gridColor else violetGlowColor
                    drawLine(
                        color = lineAlpha,
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 1.5f
                    )
                    x += gridSpacing
                }

                // Ambient corner glows
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x1F00F2FE), Color.Transparent),
                        center = Offset(0f, 0f),
                        radius = size.width * 0.5f
                    ),
                    radius = size.width * 0.5f,
                    center = Offset(0f, 0f)
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x1F8A2BE2), Color.Transparent),
                        center = Offset(size.width, size.height),
                        radius = size.width * 0.6f
                    ),
                    radius = size.width * 0.6f,
                    center = Offset(size.width, size.height)
                )
            }
    ) {
        content()
    }
}

@Composable
fun GlassMorphismCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    borderWidth: Dp = 1.dp,
    glowing: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = if (glowing) listOf(NeonCyan, SparkViolet) else listOf(TranslucentBorder, Color(0x118A2BE2))
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(ObsidianCard)
            .border(
                width = borderWidth,
                brush = gradientBrush,
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(16.dp),
        content = content
    )
}

@Composable
fun HolographicAvatar(
    colorIndex: Int,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    online: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_avatar")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_avatar_scale"
    )

    // Select color based on index
    val startColor = when (colorIndex) {
        1 -> Color(0xFFF43F5E) // Red Rose
        2 -> NeonViolet       // Violet
        3 -> SparkViolet      // Blue Spark
        4 -> Color(0xFFF59E0B) // Amber
        else -> NeonCyan      // Cyan
    }
    val endColor = when (colorIndex) {
        1 -> Color(0xFFEC4899)
        2 -> Color(0xFF3B82F6)
        3 -> NeonCyan
        4 -> Color(0xFF10B981)
        else -> NeonViolet
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        if (online) {
            // Pulse outer ring
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        drawCircle(
                            color = startColor.copy(alpha = 0.15f * (2f - pulseScale)),
                            radius = (size.toPx() / 2f) * pulseScale,
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }
            )
        }

        // Inner Avatar Circle with cyber procedural lines
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size - 6.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(startColor, endColor)
                    )
                )
        ) {
            // Draw abstract internal circuitry
            Canvas(modifier = Modifier.fillMaxSize()) {
                val pathColor = Color.White.copy(alpha = 0.28f)
                drawLine(
                    color = pathColor,
                    start = Offset(0f, size.toPx() * 0.4f),
                    end = Offset(size.toPx() * 0.4f, size.toPx() * 0.4f),
                    strokeWidth = 2f
                )
                drawLine(
                    color = pathColor,
                    start = Offset(size.toPx() * 0.4f, size.toPx() * 0.4f),
                    end = Offset(size.toPx() * 0.6f, size.toPx() * 0.7f),
                    strokeWidth = 2f
                )
                drawLine(
                    color = pathColor,
                    start = Offset(size.toPx() * 0.6f, size.toPx() * 0.7f),
                    end = Offset(size.toPx(), size.toPx() * 0.7f),
                    strokeWidth = 2f
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.5f),
                    radius = 3f,
                    center = Offset(size.toPx() * 0.4f, size.toPx() * 0.4f)
                )
            }

            // Central sleek identifier character
            val initial = if (colorIndex == 1) "V" else if (colorIndex == 2) "A" else if (colorIndex == 3) "S" else "P"
            Text(
                text = initial,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.38f).sp,
                fontFamily = FontFamily.Monospace
            )
        }

        if (online) {
            // Neon green online dot
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(HologramGreen)
                    .border(1.5.dp, ObsidianBg, CircleShape)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
fun HighTechMediaContainer(
    visualSeed: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "media_hud")
    
    // Laser scanline animation
    val scanY by infiniteTransition.animateFloat(
        initialValue = -0.1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanline_coordinate"
    )

    // Pulsing metrics values matching the sci-fi overlay
    val widgetAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInBounce),
            repeatMode = RepeatMode.Reverse
        ),
        label = "widget_frequent_blink"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF070A15)) // Jet black backplate
            .border(1.dp, Color(0x1F00F2FE), RoundedCornerShape(12.dp))
    ) {
        // Procedural vector telemetry background drawn directly on Compose Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = size.height * 0.35f
            
            // Faint concentric circles
            drawCircle(
                color = Color(0x3300F2FE),
                radius = baseRadius,
                center = center,
                style = Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
            )
            drawCircle(
                color = Color(0x1F4FACFE),
                radius = baseRadius * 1.5f,
                center = center,
                style = Stroke(width = 1.5f)
            )
            drawCircle(
                color = Color(0x108A2BE2),
                radius = baseRadius * 0.6f,
                center = center,
                style = Stroke(width = 2f)
            )

            // Dynamic grid network mapping based on seed
            val randomNodes = when (visualSeed % 3) {
                0 -> listOf(
                    Offset(0.2f, 0.3f), Offset(0.35f, 0.45f), Offset(0.48f, 0.28f), 
                    Offset(0.65f, 0.62f), Offset(0.8f, 0.4f), Offset(0.55f, 0.8f)
                )
                1 -> listOf(
                    Offset(0.15f, 0.7f), Offset(0.3f, 0.35f), Offset(0.5f, 0.6f), 
                    Offset(0.7f, 0.25f), Offset(0.85f, 0.68f), Offset(0.62f, 0.82f)
                )
                else -> listOf(
                    Offset(0.25f, 0.8f), Offset(0.4f, 0.2f), Offset(0.55f, 0.5f), 
                    Offset(0.72f, 0.85f), Offset(0.82f, 0.3f), Offset(0.12f, 0.45f)
                )
            }

            // Draw interconnections (constellation paths)
            for (i in 0 until randomNodes.size - 1) {
                val start = Offset(randomNodes[i].x * size.width, randomNodes[i].y * size.height)
                val end = Offset(randomNodes[i + 1].x * size.width, randomNodes[i + 1].y * size.height)
                drawLine(
                    brush = Brush.linearGradient(listOf(Color(0x3A00F2FE), Color(0x3A8A2BE2))),
                    start = start,
                    end = end,
                    strokeWidth = 1f
                )
            }

            // Draw holographic node points
            randomNodes.forEachIndexed { idx, point ->
                val nodePos = Offset(point.x * size.width, point.y * size.height)
                drawCircle(
                    color = if (idx == 2) Color(0xFF00F2FE) else Color(0xAA8A2BE2),
                    radius = if (idx == 2) 5f else 3.5f,
                    center = nodePos
                )
                if (idx == 2) {
                    drawCircle(
                        color = Color(0x4400F2FE),
                        radius = 12f,
                        center = nodePos,
                        style = Stroke(width = 1f)
                    )
                }
            }

            // Cyber Targetlock brackets in corners
            val pad = 12.dp.toPx()
            val cap = 14.dp.toPx()
            // Top-Left target bracket
            drawLine(Color(0xFF00F2FE), Offset(pad, pad), Offset(pad + cap, pad), strokeWidth = 2f)
            drawLine(Color(0xFF00F2FE), Offset(pad, pad), Offset(pad, pad + cap), strokeWidth = 2f)
            // Top-Right
            drawLine(Color(0xFF00F2FE), Offset(size.width - pad, pad), Offset(size.width - pad - cap, pad), strokeWidth = 2f)
            drawLine(Color(0xFF00F2FE), Offset(size.width - pad, pad), Offset(size.width - pad, pad + cap), strokeWidth = 2f)
            // Bottom-Left
            drawLine(Color(0xFF00F2FE), Offset(pad, size.height - pad), Offset(pad + cap, size.height - pad), strokeWidth = 2f)
            drawLine(Color(0xFF00F2FE), Offset(pad, size.height - pad), Offset(pad, size.height - pad - cap), strokeWidth = 2f)
            // Bottom-Right
            drawLine(Color(0xFF00F2FE), Offset(size.width - pad, size.height - pad), Offset(size.width - pad - cap, size.height - pad), strokeWidth = 2f)
            drawLine(Color(0xFF00F2FE), Offset(size.width - pad, size.height - pad), Offset(size.width - pad, size.height - pad - cap), strokeWidth = 2f)

            // Scanning Line Sweeping effect
            val scanYPos = scanY * size.height
            drawLine(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color(0x6600F2FE), Color.Transparent),
                    startY = scanYPos - 15f,
                    endY = scanYPos + 15f
                ),
                start = Offset(0f, scanYPos),
                end = Offset(size.width, scanYPos),
                strokeWidth = 3f
            )
        }

        // Space Coordinates metadata overlay
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .background(Color(0x73000000), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "CORR: [NODE_${visualSeed}_LAT]",
                color = NeonCyan,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "RESONANCE: ${300 + (visualSeed % 700)} THZ",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        // Live HUD Metrics bottom bar
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(Color(0xBD0D1527))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(TerminalGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "MATRIX SEC LEVEL: ALPHA",
                    color = TerminalText,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Text(
                text = "FPS: 60 // PACKET_SYNC: OK",
                color = NeonCyan.copy(alpha = widgetAlpha),
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GlowButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(NeonCyan, SparkViolet)
                )
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(
            text = text.uppercase(),
            color = Color.Black,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

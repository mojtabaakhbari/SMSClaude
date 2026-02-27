package com.smsclaude.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.smsclaude.ui.theme.StatusGreen

@Composable
fun PulsingDot(
    modifier: Modifier = Modifier,
    color: Color = StatusGreen,
    size: Dp = 10.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(size * 2.5f)
                .scale(scale)
                .background(color.copy(alpha = alpha * 0.3f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(size)
                .background(color, CircleShape)
        )
    }
}

@Composable
fun StaticDot(
    modifier: Modifier = Modifier,
    color: Color,
    size: Dp = 8.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .background(color, CircleShape)
    )
}

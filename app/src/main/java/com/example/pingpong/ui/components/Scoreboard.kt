package com.example.pingpong.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pingpong.ui.theme.PixelFontFamily
import kotlinx.coroutines.delay

/**
 * Muestra el marcador de ambos jugadores.
 * Si maxScore > 0 muestra el formato "P1: 3/7" para comunicar el objetivo.
 * Si isTwoPlayer es false, la etiqueta derecha muestra "CPU" en lugar de "P2".
 */
@Composable
fun Scoreboard(
    p1Score: Int,
    p2Score: Int,
    modifier: Modifier = Modifier,
    maxScore: Int = 0,
    isTwoPlayer: Boolean = false,
) {
    val rightLabel = if (isTwoPlayer) "P2" else "CPU"

    var p1ScaleTarget by remember { mutableStateOf(1f) }
    LaunchedEffect(p1Score) { p1ScaleTarget = 1.35f; delay(80); p1ScaleTarget = 1f }
    val p1Scale by animateFloatAsState(
        targetValue = p1ScaleTarget,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "p1ScoreScale"
    )

    var p2ScaleTarget by remember { mutableStateOf(1f) }
    LaunchedEffect(p2Score) { p2ScaleTarget = 1.35f; delay(80); p2ScaleTarget = 1f }
    val p2Scale by animateFloatAsState(
        targetValue = p2ScaleTarget,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "p2ScoreScale"
    )

    Row(
        modifier = modifier.fillMaxWidth().padding(top = 16.dp, start = 12.dp, end = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = if (maxScore > 0) "P1: $p1Score/$maxScore" else "P1: $p1Score",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PixelFontFamily,
            modifier = Modifier.graphicsLayer { scaleX = p1Scale; scaleY = p1Scale }
        )
        Text(
            text = if (maxScore > 0) "$rightLabel: $p2Score/$maxScore" else "$rightLabel: $p2Score",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PixelFontFamily,
            modifier = Modifier.graphicsLayer { scaleX = p2Scale; scaleY = p2Scale }
        )
    }
}

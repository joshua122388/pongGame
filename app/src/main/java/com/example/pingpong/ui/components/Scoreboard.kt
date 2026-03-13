package com.example.pingpong.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Muestra el marcador de ambos jugadores.
 * Si maxScore > 0 muestra el formato "P1: 3/7" para comunicar el objetivo.
 */
@Composable
fun Scoreboard(
    p1Score: Int,
    p2Score: Int,
    modifier: Modifier = Modifier,
    maxScore: Int = 0,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(top = 16.dp, start = 12.dp, end = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = if (maxScore > 0) "P1: $p1Score/$maxScore" else "P1: $p1Score",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (maxScore > 0) "P2: $p2Score/$maxScore" else "P2: $p2Score",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

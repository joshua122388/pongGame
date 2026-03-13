package com.example.pingpong.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pingpong.model.Difficulty
import com.example.pingpong.ui.theme.PixelFontFamily

@Composable
fun DifficultySelector(
    selected: Difficulty?,
    onSelected: (Difficulty) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Difficulty.entries.forEach { diff ->
            val isSel = diff == selected
            val bgColor by animateColorAsState(
                targetValue = if (isSel) Color(0xFF1B5E20) else Color.Transparent,
                animationSpec = tween(200, easing = FastOutSlowInEasing),
                label = "selectorBg"
            )
            OutlinedButton(
                onClick = { if (!isSel) onSelected(diff) },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = bgColor,
                    contentColor = Color.White
                ),
                border = BorderStroke(2.dp, Color.White),
                contentPadding = PaddingValues(vertical = 14.dp, horizontal = 20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val label = when (diff) {
                    Difficulty.EASY -> "Fácil"
                    Difficulty.MEDIUM -> "Medio"
                    Difficulty.HARD -> "Difícil"
                }
                Text(
                    text = label,
                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                    fontFamily = PixelFontFamily,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

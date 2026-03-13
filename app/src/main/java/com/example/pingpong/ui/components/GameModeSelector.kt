package com.example.pingpong.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.pingpong.game.GameMode
import com.example.pingpong.ui.theme.PixelFontFamily

/** Modos que aparecen en el menú de selección (los online se habilitan en una fase futura). */
private val SELECTABLE_MODES = listOf(GameMode.SINGLE_PLAYER, GameMode.LOCAL_MULTIPLAYER)

/**
 * Selector de modo de juego: muestra solo los modos actualmente disponibles.
 */
@Composable
fun GameModeSelector(
    selected: GameMode,
    onSelected: (GameMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SELECTABLE_MODES.forEach { mode ->
            val isSel = mode == selected
            val bgColor by animateColorAsState(
                targetValue = if (isSel) Color(0xFF1B5E20) else Color.Transparent,
                animationSpec = tween(200, easing = FastOutSlowInEasing),
                label = "selectorBg"
            )
            OutlinedButton(
                onClick = { if (!isSel) onSelected(mode) },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = bgColor,
                    contentColor = Color.White
                ),
                border = BorderStroke(2.dp, Color.White),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = mode.label,
                    fontFamily = PixelFontFamily,
                    fontSize = 14.sp,
                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

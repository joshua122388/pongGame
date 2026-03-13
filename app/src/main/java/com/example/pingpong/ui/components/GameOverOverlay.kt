package com.example.pingpong.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pingpong.game.Winner
import com.example.pingpong.ui.theme.PixelFontFamily

/**
 * Overlay de pantalla completa que muestra el ganador y el marcador final.
 *
 * @param winner       Resultado semántico de la partida (PLAYER_1, PLAYER_2 o CPU).
 * @param leftScore    Puntos acumulados por P1.
 * @param rightScore   Puntos acumulados por P2/CPU.
 * @param onPlayAgain  Callback al pulsar "Jugar de nuevo".
 * @param onMainMenu   Callback al pulsar "Menú principal".
 */
@Composable
fun GameOverOverlay(
    winner: Winner,
    leftScore: Int,
    rightScore: Int,
    onPlayAgain: () -> Unit,
    onMainMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val winnerName = when (winner) {
        Winner.PLAYER_1 -> "Jugador 1"
        Winner.PLAYER_2 -> "Jugador 2"
        Winner.CPU      -> "CPU"
        Winner.NONE     -> return   // no debería mostrarse con NONE
    }

    val winnerColor = when (winner) {
        Winner.PLAYER_1 -> Color(0xFF4CAF50)   // verde
        Winner.PLAYER_2 -> Color(0xFF2196F3)   // azul
        Winner.CPU      -> Color(0xFFFF9800)   // naranja
        Winner.NONE     -> Color.White
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.82f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "¡JUEGO TERMINADO!",
                color = Color.White,
                fontFamily = PixelFontFamily,
                fontSize = 24.sp,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "🏆  $winnerName Gana  🏆",
                color = winnerColor,
                fontFamily = PixelFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "$leftScore — $rightScore",
                color = Color.White,
                fontFamily = PixelFontFamily,
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            val playAgainInteraction = remember { MutableInteractionSource() }
            val isPlayAgainPressed by playAgainInteraction.collectIsPressedAsState()
            val playAgainScale by animateFloatAsState(
                targetValue = if (isPlayAgainPressed) 0.94f else 1f,
                animationSpec = spring(stiffness = Spring.StiffnessHigh),
                label = "btnScale"
            )
            OutlinedButton(
                onClick = onPlayAgain,
                border = BorderStroke(2.dp, Color.White),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(vertical = 14.dp, horizontal = 40.dp),
                interactionSource = playAgainInteraction,
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .graphicsLayer { scaleX = playAgainScale; scaleY = playAgainScale }
            ) {
                Text(text = "Jugar de Nuevo", fontFamily = PixelFontFamily, fontSize = 17.sp)
            }

            val menuInteraction = remember { MutableInteractionSource() }
            val isMenuPressed by menuInteraction.collectIsPressedAsState()
            val menuScale by animateFloatAsState(
                targetValue = if (isMenuPressed) 0.94f else 1f,
                animationSpec = spring(stiffness = Spring.StiffnessHigh),
                label = "btnScale"
            )
            OutlinedButton(
                onClick = onMainMenu,
                border = BorderStroke(2.dp, Color.White),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(vertical = 14.dp, horizontal = 40.dp),
                interactionSource = menuInteraction,
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .graphicsLayer { scaleX = menuScale; scaleY = menuScale }
            ) {
                Text(text = "Menú Principal", fontFamily = PixelFontFamily, fontSize = 17.sp)
            }
        }
    }
}

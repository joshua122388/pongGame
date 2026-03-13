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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pingpong.ui.theme.PixelFontFamily

/**
 * Overlay de pausa que cubre la pantalla con opciones de continuar o volver al menú.
 */
@Composable
fun PauseOverlay(
    onResume: () -> Unit,
    onMainMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.60f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "PAUSA",
                color = Color.White,
                fontFamily = PixelFontFamily,
                fontSize = 40.sp,
                letterSpacing = 4.sp
            )

            val resumeInteraction = remember { MutableInteractionSource() }
            val isResumePressed by resumeInteraction.collectIsPressedAsState()
            val resumeScale by animateFloatAsState(
                targetValue = if (isResumePressed) 0.94f else 1f,
                animationSpec = spring(stiffness = Spring.StiffnessHigh),
                label = "btnScale"
            )
            OutlinedButton(
                onClick = onResume,
                border = BorderStroke(2.dp, Color.White),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(vertical = 14.dp, horizontal = 48.dp),
                interactionSource = resumeInteraction,
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .graphicsLayer { scaleX = resumeScale; scaleY = resumeScale }
            ) {
                Text(text = "Continuar", fontFamily = PixelFontFamily, fontSize = 18.sp)
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
                contentPadding = PaddingValues(vertical = 14.dp, horizontal = 48.dp),
                interactionSource = menuInteraction,
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .graphicsLayer { scaleX = menuScale; scaleY = menuScale }
            ) {
                Text(text = "Menú Principal", fontFamily = PixelFontFamily, fontSize = 18.sp)
            }
        }
    }
}

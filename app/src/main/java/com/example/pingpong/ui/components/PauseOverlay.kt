package com.example.pingpong.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

            OutlinedButton(
                onClick = onResume,
                border = BorderStroke(2.dp, Color.White),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(vertical = 14.dp, horizontal = 48.dp),
                modifier = Modifier.fillMaxWidth(0.65f)
            ) {
                Text(text = "Continuar", fontFamily = PixelFontFamily, fontSize = 18.sp)
            }

            OutlinedButton(
                onClick = onMainMenu,
                border = BorderStroke(2.dp, Color.White),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(vertical = 14.dp, horizontal = 48.dp),
                modifier = Modifier.fillMaxWidth(0.65f)
            ) {
                Text(text = "Menú Principal", fontFamily = PixelFontFamily, fontSize = 18.sp)
            }
        }
    }
}


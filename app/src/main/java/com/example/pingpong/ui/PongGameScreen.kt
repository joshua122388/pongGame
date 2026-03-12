package com.example.pingpong.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import com.example.pingpong.game.GameState
import com.example.pingpong.model.Difficulty
import com.example.pingpong.ui.components.DifficultySelector
import com.example.pingpong.ui.components.PlayerLabels
import com.example.pingpong.ui.components.Scoreboard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.util.Log
import android.app.Activity
import androidx.compose.foundation.BorderStroke
import com.example.pingpong.ui.theme.PixelFontFamily
import androidx.compose.ui.unit.sp

/**
 * Interfaz clásica de Pong con bucle de juego básico y controles.
 */
@Composable
fun PongGameScreen(
    modifier: Modifier = Modifier,
    contentPadding: androidx.compose.foundation.layout.PaddingValues = androidx.compose.foundation.layout.PaddingValues(0.dp)
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
            PongBoard()
        }
    }
}

@Composable
private fun PongBoard() {
    // Dimensiones del tablero desde Canvas
    var boardWidth by remember { mutableStateOf(0f) }
    var boardHeight by remember { mutableStateOf(0f) }

    // Estado del menú/juego
    var selectedDifficulty by remember { mutableStateOf<Difficulty?>(null) }
    var started by remember { mutableStateOf(false) }
    var gameInitCounter by remember { mutableStateOf(0) }

    var game by remember { mutableStateOf<GameState?>(null) }

    // Tick de frame para activar recomposición/dibujado
    var frameTick by remember { mutableStateOf(0L) }

    // Inicializar juego solo después de iniciar y cuando el tamaño es conocido
    LaunchedEffect(gameInitCounter, boardWidth, boardHeight) {
        try {
            if (gameInitCounter > 0 && selectedDifficulty != null && boardWidth > 0f && boardHeight > 0f) {
                // Siempre reinicializar el juego cuando se presiona iniciar
                val g = GameState(boardWidth, boardHeight, selectedDifficulty!!)
                g.initialize()
                game = g
                started = true
            }
        } catch (t: Throwable) {
            Log.e("PongGame", "Error during game initialization", t)
            // Prevenir que el bucle continúe en un estado roto
            started = false
            game = null
        }
    }

    // Bucle principal del juego: avanzar pelota, manejar puntuación, cambiar color al anotar
    LaunchedEffect(game, started) {
        try {
            val g = game ?: return@LaunchedEffect
            if (!started) return@LaunchedEffect
            var lastTime = 0L
            while (true) {
                val t = withFrameNanos { it }
                if (lastTime == 0L) {
                    lastTime = t
                    frameTick = t // asegurar primer dibujado
                    continue
                }
                val dt = (t - lastTime) / 1_000_000_000f
                lastTime = t

                val event = g.update(dt)
                if (event != null) {
                    // Cambiar pelota a verde brevemente para indicar que se anotó un punto
                    g.ball = g.ball.copy(color = Color(0xFF2E7D32))
                    // Resetear a blanco después sin bloquear el bucle
                    launch {
                        delay(220)
                        g.ball = g.ball.copy(color = Color.White)
                    }
                }
                // Activar redibujado de UI
                frameTick = t
            }
        } catch (t: Throwable) {
            Log.e("PongGame", "Uncaught exception in game loop", t)
            started = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .onSizeChanged {
                    boardWidth = it.width.toFloat()
                    boardHeight = it.height.toFloat()
                    // Actualizar límites internos del tablero si ya fue creado
                    game?.let { g ->
                        g.boardWidth = boardWidth
                        g.boardHeight = boardHeight
                    }
                }
        ) {
            val tick = frameTick
            val phase = if (tick >= 0L) 0f else 0f
            val g = game
            if (g == null || !started) return@Canvas

            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 12f), phase)
            // Línea punteada del medio
            drawLine(
                color = Color.DarkGray,
                start = Offset(size.width / 2f, 0f),
                end = Offset(size.width / 2f, size.height),
                strokeWidth = 6f,
                pathEffect = pathEffect
            )

            // Paleta izquierda
            drawRect(
                color = Color.White,
                topLeft = Offset(x = g.leftPaddle.x, y = g.leftPaddle.y),
                size = androidx.compose.ui.geometry.Size(g.leftPaddle.width, g.leftPaddle.height),
                style = Fill
            )
            // Paleta derecha
            drawRect(
                color = Color.White,
                topLeft = Offset(x = g.rightPaddle.x, y = g.rightPaddle.y),
                size = androidx.compose.ui.geometry.Size(g.rightPaddle.width, g.rightPaddle.height),
                style = Fill
            )

            // Pelota
            drawCircle(
                color = g.ball.color,
                radius = g.ball.radius,
                center = g.ball.center,
                style = Fill
            )
        }


        if (started) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxSize()
                    .align(Alignment.CenterStart)
                    .pointerInput(boardHeight) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                game?.let { g ->
                                    g.leftPaddle = g.leftPaddle.copy(y = g.clampPaddleY(g.leftPaddle.y + dragAmount.y))
                                }
                            }
                        )
                    }
            )
            // Lado derecho de entrada deshabilitado: controlado por CPU
        }

        // Overlays encima durante el juego
        if (started) {
            @Suppress("UNUSED_VARIABLE")
            val tick = frameTick
            Column(modifier = Modifier.align(Alignment.TopCenter).padding(top = 4.dp).fillMaxWidth()) {
                PlayerLabels()
                Scoreboard(
                    p1Score = game?.leftScore ?: 0,
                    p2Score = game?.rightScore ?: 0
                )
            }

            // Botón Salir en la esquina inferior derecha durante el gameplay
            OutlinedButton(
                onClick = {
                    started = false
                    game = null
                },
                border = BorderStroke(1.5.dp, Color.White),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Black.copy(alpha = 0.6f),
                    contentColor = Color.White
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 16.dp, end = 16.dp)
            ) {
                Text(
                    text = "Salir",
                    fontFamily = PixelFontFamily,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp
                )
            }
        }

        // Overlay del menú de inicio
        if (!started) {
            val activity = LocalContext.current as? Activity
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título en la parte superior
                Text(
                    text = "Pong Game",
                    color = Color.White,
                    fontFamily = PixelFontFamily,
                    fontSize = 32.sp,
                    modifier = Modifier.padding(top = 40.dp, bottom = 32.dp)
                )

                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Selecciona la dificultad",
                            color = Color.White,
                            fontFamily = PixelFontFamily,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        DifficultySelector(
                            selected = selectedDifficulty,
                            onSelected = { selectedDifficulty = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                // Botones de acción en la parte inferior
                Column(
                    modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botón Iniciar (solo visible cuando hay una dificultad seleccionada)
                    if (selectedDifficulty != null) {
                        OutlinedButton(
                            onClick = { gameInitCounter++ },
                            border = BorderStroke(2.dp, Color.White),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent, contentColor = Color.White),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 14.dp, horizontal = 20.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Iniciar", fontFamily = PixelFontFamily, fontSize = 18.sp, letterSpacing = 1.5.sp)
                        }
                    }
                    // Botón Salir (siempre visible)
                    OutlinedButton(
                        onClick = { activity?.finish() },
                        border = BorderStroke(2.dp, Color.White),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent, contentColor = Color.White),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 14.dp, horizontal = 20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Salir", fontFamily = PixelFontFamily, fontSize = 18.sp, letterSpacing = 1.5.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PongGamePreview() {
    PongGameScreen()
}
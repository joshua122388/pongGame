package com.example.pingpong.ui

import android.app.Activity
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pingpong.game.GameMode
import com.example.pingpong.game.GameState
import com.example.pingpong.game.Winner
import com.example.pingpong.model.Difficulty
import com.example.pingpong.ui.components.DifficultySelector
import com.example.pingpong.ui.components.GameModeSelector
import com.example.pingpong.ui.components.GameOverOverlay
import com.example.pingpong.ui.components.PauseOverlay
import com.example.pingpong.ui.components.PlayerLabels
import com.example.pingpong.ui.components.Scoreboard
import com.example.pingpong.ui.components.WinPointsSelector
import com.example.pingpong.ui.theme.PixelFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Interfaz clásica de Pong con bucle de juego básico y controles.
 */
@Composable
fun PongGameScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
            PongBoard()
        }
    }
}

@Composable
private fun PongBoard() {
    // Dimensiones del tablero
    var boardWidth by remember { mutableStateOf(0f) }
    var boardHeight by remember { mutableStateOf(0f) }

    // Opciones del menú
    var selectedDifficulty by remember { mutableStateOf<Difficulty?>(null) }
    var selectedGameMode by remember { mutableStateOf(GameMode.SINGLE_PLAYER) }
    var selectedMaxScore by remember { mutableStateOf(7) }
    var isWinScoreValid by remember { mutableStateOf(true) }  // 7 (valor inicial) es válido

    // Estado de la partida
    var started by remember { mutableStateOf(false) }
    var gameInitCounter by remember { mutableStateOf(0) }
    var isPaused by remember { mutableStateOf(false) }
    var game by remember { mutableStateOf<GameState?>(null) }

    // Tick de frame → fuerza recomposición del Canvas y el HUD
    var frameTick by remember { mutableStateOf(0L) }

    // En modo local la dificultad no es relevante; usamos MEDIUM por defecto
    val effectiveDifficulty =
        if (selectedGameMode == GameMode.LOCAL_MULTIPLAYER) Difficulty.MEDIUM else selectedDifficulty

    // Congelar estado de fin de juego para que sobreviva la animación de salida
    var frozenWinner by remember { mutableStateOf(Winner.NONE) }
    var frozenLeftScore by remember { mutableStateOf(0) }
    var frozenRightScore by remember { mutableStateOf(0) }
    SideEffect {
        val g = game
        if (g != null && g.winner != Winner.NONE) {
            frozenWinner = g.winner
            frozenLeftScore = g.leftScore
            frozenRightScore = g.rightScore
        }
    }

    // ── Inicialización del juego ──────────────────────────────────────────────
    LaunchedEffect(gameInitCounter, boardWidth, boardHeight) {
        try {
            if (gameInitCounter > 0 && effectiveDifficulty != null
                && boardWidth > 0f && boardHeight > 0f
            ) {
                val g = GameState(
                    boardWidth = boardWidth,
                    boardHeight = boardHeight,
                    difficulty = effectiveDifficulty,
                    gameMode = selectedGameMode,
                    maxScore = selectedMaxScore,
                )
                g.initialize()
                game = g
                started = true
                isPaused = false
            }
        } catch (t: Throwable) {
            Log.e("PongGame", "Error during game initialization", t)
            started = false
            game = null
        }
    }

    // Sincronizar isPaused con el GameState para que update() nunca corra en pausa
    LaunchedEffect(isPaused, game) {
        game?.isPaused = isPaused
    }

    // ── Bucle principal del juego ─────────────────────────────────────────────
    LaunchedEffect(game, started) {
        try {
            val g = game ?: return@LaunchedEffect
            if (!started) return@LaunchedEffect
            var lastTime = 0L
            while (true) {
                val t = withFrameNanos { it }
                if (lastTime == 0L) { lastTime = t; frameTick = t; continue }
                val dt = (t - lastTime) / 1_000_000_000f
                lastTime = t

                // Solo actualizar si no está pausado y nadie ha ganado aún
                if (!g.isPaused && g.winner == Winner.NONE) {
                    val event = g.update(dt)
                    if (event != null) {
                        // Flash verde en la pelota al anotar
                        g.ball = g.ball.copy(color = Color(0xFF2E7D32))
                        launch {
                            delay(220)
                            if (g.winner == Winner.NONE) g.ball = g.ball.copy(color = Color.White)
                        }
                    }
                }
                frameTick = t
            }
        } catch (t: Throwable) {
            Log.e("PongGame", "Uncaught exception in game loop", t)
            started = false
        }
    }

    // ── UI ───────────────────────────────────────────────────────────────────
    // Read frameTick here so PongBoard itself recomposes every frame while the game
    // is running, keeping all AnimatedVisibility `visible` conditions up-to-date.
    @Suppress("UNUSED_VARIABLE") val tick = frameTick
    Box(modifier = Modifier.fillMaxSize()) {

        // ── Canvas: tablero ──────────────────────────────────────────────────
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .onSizeChanged {
                    boardWidth = it.width.toFloat()
                    boardHeight = it.height.toFloat()
                    game?.let { g ->
                        g.boardWidth = boardWidth
                        g.boardHeight = boardHeight
                    }
                }
        ) {
            @Suppress("UNUSED_VARIABLE") val tick = frameTick
            val g = game
            if (g == null || !started) return@Canvas

            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 12f), 0f)
            drawLine(
                color = Color.DarkGray,
                start = Offset(size.width / 2f, 0f),
                end = Offset(size.width / 2f, size.height),
                strokeWidth = 6f,
                pathEffect = pathEffect
            )
            drawRect(
                color = Color.White,
                topLeft = Offset(g.leftPaddle.x, g.leftPaddle.y),
                size = androidx.compose.ui.geometry.Size(g.leftPaddle.width, g.leftPaddle.height),
                style = Fill
            )
            drawRect(
                color = Color.White,
                topLeft = Offset(g.rightPaddle.x, g.rightPaddle.y),
                size = androidx.compose.ui.geometry.Size(g.rightPaddle.width, g.rightPaddle.height),
                style = Fill
            )
            drawCircle(color = g.ball.color, radius = g.ball.radius, center = g.ball.center, style = Fill)
        }

        // ── Áreas táctiles (fuera de AnimatedVisibility para que siempre se midan) ──
        if (started) {
            // Mitad izquierda → Jugador 1
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxSize()
                    .align(Alignment.CenterStart)
                    .pointerInput(boardHeight) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            game?.let { g ->
                                g.leftPaddle = g.leftPaddle.copy(
                                    y = g.clampPaddleY(g.leftPaddle.y + dragAmount.y)
                                )
                            }
                        }
                    }
            )
            // Mitad derecha → Jugador 2 (solo en modo local)
            if (selectedGameMode == GameMode.LOCAL_MULTIPLAYER) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .fillMaxSize()
                        .align(Alignment.CenterEnd)
                        .pointerInput(boardHeight) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                game?.let { g ->
                                    g.rightPaddle = g.rightPaddle.copy(
                                        y = g.clampRightPaddleY(g.rightPaddle.y + dragAmount.y)
                                    )
                                }
                            }
                        }
                )
            }
        }

        // ── HUD durante el juego ─────────────────────────────────────────────
        AnimatedVisibility(
            visible = started && game != null,
            enter = fadeIn(tween(350, delayMillis = 150)),
            exit = fadeOut(tween(200))
        ) {
            @Suppress("UNUSED_VARIABLE") val tick = frameTick
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 4.dp)
                        .fillMaxWidth()
                ) {
                    PlayerLabels(isTwoPlayer = selectedGameMode == GameMode.LOCAL_MULTIPLAYER)
                    Scoreboard(
                        p1Score = game?.leftScore ?: 0,
                        p2Score = game?.rightScore ?: 0,
                        maxScore = selectedMaxScore,
                        isTwoPlayer = selectedGameMode == GameMode.LOCAL_MULTIPLAYER
                    )
                }

                // Botón Pausa (esquina superior izquierda, bajo el marcador)
                val pauseInteraction = remember { MutableInteractionSource() }
                val isPausePressed by pauseInteraction.collectIsPressedAsState()
                val pauseScale by animateFloatAsState(
                    targetValue = if (isPausePressed) 0.94f else 1f,
                    animationSpec = spring(stiffness = Spring.StiffnessHigh),
                    label = "btnScale"
                )
                OutlinedButton(
                    onClick = { isPaused = !isPaused },
                    border = BorderStroke(1.5.dp, Color.White),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.6f),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    interactionSource = pauseInteraction,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 80.dp, start = 8.dp)
                        .graphicsLayer { scaleX = pauseScale; scaleY = pauseScale }
                ) {
                    Text(
                        text = if (isPaused) "▶" else "⏸",
                        fontFamily = PixelFontFamily,
                        fontSize = 16.sp
                    )
                }

                // Botón Salir (esquina inferior derecha)
                val exitInteraction = remember { MutableInteractionSource() }
                val isExitPressed by exitInteraction.collectIsPressedAsState()
                val exitScale by animateFloatAsState(
                    targetValue = if (isExitPressed) 0.94f else 1f,
                    animationSpec = spring(stiffness = Spring.StiffnessHigh),
                    label = "btnScale"
                )
                OutlinedButton(
                    onClick = {
                        started = false
                        game = null
                        isPaused = false
                    },
                    border = BorderStroke(1.5.dp, Color.White),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.6f),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    interactionSource = exitInteraction,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 16.dp, end = 16.dp)
                        .graphicsLayer { scaleX = exitScale; scaleY = exitScale }
                ) {
                    Text(text = "Salir", fontFamily = PixelFontFamily, fontSize = 14.sp, letterSpacing = 1.sp)
                }
            }
        }

        // ── Overlay de pausa ─────────────────────────────────────────────────
        AnimatedVisibility(
            visible = started && isPaused && game?.winner == Winner.NONE,
            enter = fadeIn(tween(180)),
            exit = fadeOut(tween(150))
        ) {
            PauseOverlay(
                onResume = { isPaused = false },
                onMainMenu = {
                    started = false
                    game = null
                    isPaused = false
                }
            )
        }

        // ── Overlay de fin de juego ──────────────────────────────────────────
        val currentGame = game
        AnimatedVisibility(
            visible = started && currentGame != null && currentGame.winner != Winner.NONE,
            enter = fadeIn(tween(400)) + scaleIn(
                initialScale = 0.88f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            ),
            exit = fadeOut(tween(200))
        ) {
            @Suppress("UNUSED_VARIABLE") val tick = frameTick
            GameOverOverlay(
                winner = frozenWinner,
                leftScore = frozenLeftScore,
                rightScore = frozenRightScore,
                onPlayAgain = { gameInitCounter++ },
                onMainMenu = {
                    started = false
                    game = null
                }
            )
        }

        // ── Menú de inicio ───────────────────────────────────────────────────
        AnimatedVisibility(
            visible = !started,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(250)) + slideOutVertically(
                targetOffsetY = { -it / 4 },
                animationSpec = tween(250, easing = FastOutSlowInEasing)
            )
        ) {
            val activity = LocalContext.current as? Activity
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Pong Game",
                    color = Color.White,
                    fontFamily = PixelFontFamily,
                    fontSize = 32.sp,
                    modifier = Modifier.padding(top = 40.dp, bottom = 28.dp)
                )

                // Selector de modo de juego
                Text(
                    text = "Modo de juego",
                    color = Color.White,
                    fontFamily = PixelFontFamily,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                GameModeSelector(
                    selected = selectedGameMode,
                    onSelected = { mode ->
                        selectedGameMode = mode
                        if (mode == GameMode.LOCAL_MULTIPLAYER) selectedDifficulty = Difficulty.MEDIUM
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        // Selector de dificultad (solo en 1 jugador)
                        AnimatedVisibility(
                            visible = selectedGameMode == GameMode.SINGLE_PLAYER,
                            enter = fadeIn(tween(200)) + expandVertically(tween(220, easing = FastOutSlowInEasing)),
                            exit = fadeOut(tween(160)) + shrinkVertically(tween(180, easing = FastOutLinearInEasing))
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Selecciona la dificultad",
                                    color = Color.White,
                                    fontFamily = PixelFontFamily,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                DifficultySelector(
                                    selected = selectedDifficulty,
                                    onSelected = { selectedDifficulty = it },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }

                        // Selector de puntos para ganar
                        Text(
                            text = "Puntos para ganar",
                            color = Color.White,
                            fontFamily = PixelFontFamily,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        WinPointsSelector(
                            selected = selectedMaxScore,
                            onSelected = { selectedMaxScore = it },
                            onValidityChange = { isWinScoreValid = it },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                        )
                    }
                }

                // Botones de acción
                val canStart = isWinScoreValid &&
                        (selectedGameMode == GameMode.LOCAL_MULTIPLAYER || selectedDifficulty != null)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (canStart) {
                        val startInteraction = remember { MutableInteractionSource() }
                        val isStartPressed by startInteraction.collectIsPressedAsState()
                        val startScale by animateFloatAsState(
                            targetValue = if (isStartPressed) 0.94f else 1f,
                            animationSpec = spring(stiffness = Spring.StiffnessHigh),
                            label = "btnScale"
                        )
                        OutlinedButton(
                            onClick = { gameInitCounter++ },
                            border = BorderStroke(2.dp, Color.White),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(vertical = 14.dp, horizontal = 20.dp),
                            interactionSource = startInteraction,
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer { scaleX = startScale; scaleY = startScale }
                        ) {
                            Text(text = "Iniciar", fontFamily = PixelFontFamily, fontSize = 18.sp, letterSpacing = 1.5.sp)
                        }
                    }

                    val menuExitInteraction = remember { MutableInteractionSource() }
                    val isMenuExitPressed by menuExitInteraction.collectIsPressedAsState()
                    val menuExitScale by animateFloatAsState(
                        targetValue = if (isMenuExitPressed) 0.94f else 1f,
                        animationSpec = spring(stiffness = Spring.StiffnessHigh),
                        label = "btnScale"
                    )
                    OutlinedButton(
                        onClick = { activity?.finish() },
                        border = BorderStroke(2.dp, Color.White),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(vertical = 14.dp, horizontal = 20.dp),
                        interactionSource = menuExitInteraction,
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer { scaleX = menuExitScale; scaleY = menuExitScale }
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

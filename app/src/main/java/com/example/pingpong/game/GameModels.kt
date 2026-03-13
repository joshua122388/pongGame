package com.example.pingpong.game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random
import com.example.pingpong.model.Difficulty

/** Modelos de datos básicos y lógica de actualización para un Pong local simple de dos jugadores. */
data class Paddle(
    val x: Float,
    var y: Float,
    val width: Float,
    val height: Float,
)

data class Ball(
    var center: Offset,
    var radius: Float,
    var velocity: Offset,
    var color: Color = Color.White,
)

enum class Side { LEFT, RIGHT }

data class ScoringEvent(val scorer: Side)

class GameState(
    var boardWidth: Float,
    var boardHeight: Float,
    var difficulty: Difficulty,
    var gameMode: GameMode = GameMode.SINGLE_PLAYER,
    var maxScore: Int = 7,
    val paddleWidthRatio: Float = 0.02f,
    val paddleHeightRatio: Float = 0.12f,
    val ballRadiusRatio: Float = 0.015f,
) {
    lateinit var leftPaddle: Paddle
    lateinit var rightPaddle: Paddle
    lateinit var ball: Ball

    var leftScore: Int = 0
    var rightScore: Int = 0

    /** Resultado de la partida; NONE mientras sigue en curso. */
    var winner: Winner = Winner.NONE

    /** Cuando es true el bucle de juego omite update(). */
    var isPaused: Boolean = false

    /** Multiplicador de velocidad acumulado por golpes consecutivos (se resetea al respawnear). */
    private var accelerationMultiplier: Float = 1.0f
    private val accelerationPerHit: Float = 0.05f
    private val maxAcceleration: Float = 2.0f

    private val baseBallSpeed: Float
        get() = min(boardWidth, boardHeight) * 0.6f // px por segundo

    // --- Estado de IA (CPU controla la paleta derecha) ---
    private data class AiParams(val speed: Float, val reactionS: Float, val errorPx: Float)
    private var aiTimer: Float = 0f
    private var aiTargetY: Float? = null // Y superior-izquierdo para la paleta derecha

    private fun currentAiParams(): AiParams {
        val speedBase = baseBallSpeed
        val errorBaseH = boardHeight.coerceAtLeast(1f)
        return when (difficulty) {
            Difficulty.EASY -> AiParams(
                speed = speedBase * 0.50f,
                reactionS = 0.20f,
                errorPx = max(12f, errorBaseH * 0.05f)
            )
            Difficulty.MEDIUM -> AiParams(
                speed = speedBase * 1.4f,
                reactionS = 0.08f,
                errorPx = max(6f, errorBaseH * 0.02f)
            )
            Difficulty.HARD -> AiParams(
                speed = speedBase * 2.0f,
                reactionS = 0.04f,
                errorPx = max(2f, errorBaseH * 0.008f)
            )
        }
    }

    fun initialize() {
        val paddleW = boardWidth * paddleWidthRatio
        val paddleH = boardHeight * paddleHeightRatio
        leftPaddle = Paddle(x = paddleW, y = (boardHeight - paddleH) / 2f, width = paddleW, height = paddleH)
        rightPaddle = Paddle(x = boardWidth - (2 * paddleW), y = (boardHeight - paddleH) / 2f, width = paddleW, height = paddleH)

        val ballR = min(boardWidth, boardHeight) * ballRadiusRatio
        ball = Ball(center = Offset(boardWidth / 2f, boardHeight / 2f), radius = ballR, velocity = randomInitialVelocity())

        leftScore = 0
        rightScore = 0
        winner = Winner.NONE
        accelerationMultiplier = 1.0f
        aiTimer = 0f
        aiTargetY = clampRightPaddleY((boardHeight - rightPaddle.height) / 2f)
    }

    fun changeDifficulty(newDiff: Difficulty) {
        // Escalar velocidad para preservar aproximadamente la velocidad aparente al cambiar dificultades
        val old = difficulty
        difficulty = newDiff
        val scale = if (old.speedMultiplier == 0f) 1f else newDiff.speedMultiplier / old.speedMultiplier
        ball.velocity = Offset(ball.velocity.x * scale, ball.velocity.y * scale)
        aiTimer = 0f
    }

    fun clampPaddleY(y: Float): Float {
        val maxY = (boardHeight - leftPaddle.height).coerceAtLeast(0f)
        return y.coerceIn(0f, maxY)
    }

    fun clampRightPaddleY(y: Float): Float {
        val maxY = (boardHeight - rightPaddle.height).coerceAtLeast(0f)
        return y.coerceIn(0f, maxY)
    }

    private fun updateAi(dtSeconds: Float) {
        // La IA solo actúa en modo single player
        if (gameMode != GameMode.SINGLE_PLAYER) return

        val params = currentAiParams()
        aiTimer -= dtSeconds
        if (aiTimer <= 0f) {
            val goingRight = ball.velocity.x > 0f
            val desiredCenterY = if (goingRight) {
                val err = (Random.nextFloat() * 2f - 1f) * params.errorPx // [-err, +err]
                (ball.center.y + err)
            } else {
                boardHeight / 2f
            }
            // Convertir centro deseado a Y superior-izquierdo para posición de paleta
            val desiredTopY = desiredCenterY - rightPaddle.height / 2f
            aiTargetY = clampRightPaddleY(desiredTopY)
            aiTimer = params.reactionS
        }
        // Moverse hacia el objetivo a velocidad limitada
        val targetY = aiTargetY ?: return
        val cur = rightPaddle.y
        val delta = targetY - cur
        if (delta != 0f) {
            val step = params.speed * dtSeconds
            val newY = when {
                abs(delta) <= step -> targetY
                delta > 0f -> cur + step
                else -> cur - step
            }
            rightPaddle = rightPaddle.copy(y = clampRightPaddleY(newY))
        }
    }

    fun update(dtSeconds: Float): ScoringEvent? {
        var event: ScoringEvent? = null

        // Actualización de CPU (controla paleta derecha)
        updateAi(dtSeconds)

        // Mover pelota
        val speedScale = difficulty.speedMultiplier
        val dx = ball.velocity.x * dtSeconds * speedScale
        val dy = ball.velocity.y * dtSeconds * speedScale
        var newCenter = ball.center.copy(x = ball.center.x + dx, y = ball.center.y + dy)

        // Colisión con bordes superior/inferior
        if (newCenter.y - ball.radius < 0f) {
            newCenter = newCenter.copy(y = ball.radius)
            ball.velocity = ball.velocity.copy(y = abs(ball.velocity.y))
        } else if (newCenter.y + ball.radius > boardHeight) {
            newCenter = newCenter.copy(y = boardHeight - ball.radius)
            ball.velocity = ball.velocity.copy(y = -abs(ball.velocity.y))
        }

        // Colisiones de paletas
        val lp = leftPaddle
        val rp = rightPaddle

        // Verificar paleta izquierda
        if (ball.velocity.x < 0) {
            val paddleRight = lp.x + lp.width
            if (newCenter.x - ball.radius <= paddleRight &&
                newCenter.y >= lp.y && newCenter.y <= lp.y + lp.height &&
                ball.center.x >= paddleRight // asegurar cruce hacia la paleta
            ) {
                newCenter = newCenter.copy(x = paddleRight + ball.radius)
                // Agregar algo de deflexión basada en dónde golpeó la paleta
                val rel = ((newCenter.y - lp.y) / lp.height - 0.5f) * 2f // -1..1
                // Aceleración: cada golpe incrementa la velocidad
                accelerationMultiplier = (accelerationMultiplier + accelerationPerHit).coerceAtMost(maxAcceleration)
                val speed = max(50f, baseBallSpeed * accelerationMultiplier)
                ball.velocity = Offset(abs(speed), rel * speed)
            }
        } else if (ball.velocity.x > 0) { // Paleta derecha
            val paddleLeft = rp.x
            if (newCenter.x + ball.radius >= paddleLeft &&
                newCenter.y >= rp.y && newCenter.y <= rp.y + rp.height &&
                ball.center.x <= paddleLeft // cruzando
            ) {
                newCenter = newCenter.copy(x = paddleLeft - ball.radius)
                val rel = ((newCenter.y - rp.y) / rp.height - 0.5f) * 2f // -1..1
                accelerationMultiplier = (accelerationMultiplier + accelerationPerHit).coerceAtMost(maxAcceleration)
                val speed = max(50f, baseBallSpeed * accelerationMultiplier)
                ball.velocity = Offset(-abs(speed), rel * speed)
            }
        }

        // Puntuación fuera de límites
        if (newCenter.x + ball.radius < 0f) {
            // Derecha anota
            rightScore += 1
            event = ScoringEvent(Side.RIGHT)
            if (rightScore >= maxScore) {
                winner = if (gameMode == GameMode.SINGLE_PLAYER) Winner.CPU else Winner.PLAYER_2
                ball.center = Offset(boardWidth / 2f, boardHeight / 2f)
                newCenter = ball.center
            } else {
                respawnBall(randomDirection = true)
                newCenter = ball.center
            }
        } else if (newCenter.x - ball.radius > boardWidth) {
            // Izquierda anota
            leftScore += 1
            event = ScoringEvent(Side.LEFT)
            if (leftScore >= maxScore) {
                winner = Winner.PLAYER_1
                ball.center = Offset(boardWidth / 2f, boardHeight / 2f)
                newCenter = ball.center
            } else {
                respawnBall(randomDirection = true)
                newCenter = ball.center
            }
        }

        ball.center = newCenter
        return event
    }

    fun respawnBall(randomDirection: Boolean) {
        accelerationMultiplier = 1.0f   // Resetear aceleración al respawnear
        ball.center = Offset(boardWidth / 2f, boardHeight / 2f)
        ball.velocity = if (randomDirection) randomInitialVelocity() else randomInitialVelocity(preferRight = true)
        // No resetear color aquí; la UI puede ponerlo en verde y luego de vuelta a blanco después de un corto delay.
        aiTimer = 0f
        aiTargetY = clampRightPaddleY((boardHeight - rightPaddle.height) / 2f)
    }

    private fun randomInitialVelocity(preferRight: Boolean? = null): Offset {
        val speed = baseBallSpeed
        // Ángulo aleatorio pero evitar extremos demasiado horizontales/verticales.
        val vy = (Random.nextFloat() - 0.5f) * speed * 0.6f // ~[-0.3, 0.3] * speed
        val dir = when (preferRight) {
            true -> 1f
            false -> -1f
            null -> if (Random.nextBoolean()) 1f else -1f
        }
        val vx = dir * max(speed * 0.7f, abs(speed - abs(vy))) // mantener mayormente horizontal
        return Offset(vx, vy)
    }
}

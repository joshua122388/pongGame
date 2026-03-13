package com.example.pingpong.game

/**
 * Resultado semántico de una partida.
 *
 * NONE     → la partida sigue en curso o no ha comenzado
 * PLAYER_1 → ganó el jugador de la paleta izquierda
 * PLAYER_2 → ganó el jugador de la paleta derecha (modo LOCAL_MULTIPLAYER)
 * CPU      → ganó la CPU (modo SINGLE_PLAYER)
 */
enum class Winner {
    NONE,
    PLAYER_1,
    PLAYER_2,
    CPU
}


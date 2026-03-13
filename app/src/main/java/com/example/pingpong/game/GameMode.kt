package com.example.pingpong.game

/**
 * Modos de juego disponibles.
 *
 * SINGLE_PLAYER     → jugador vs CPU en el mismo dispositivo
 * LOCAL_MULTIPLAYER → dos jugadores en el mismo dispositivo
 * ONLINE_HOST       → partida remota, este dispositivo es el host  (Fase futura)
 * ONLINE_GUEST      → partida remota, este dispositivo es el invitado (Fase futura)
 */
enum class GameMode(val label: String) {
    SINGLE_PLAYER("1 Jugador"),
    LOCAL_MULTIPLAYER("Multijugador Local"),
    ONLINE_HOST("Online — Host"),
    ONLINE_GUEST("Online — Invitado")
}

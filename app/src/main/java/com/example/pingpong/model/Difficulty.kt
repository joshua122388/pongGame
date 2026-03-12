package com.example.pingpong.model


//Niveles de dificultad del juego.


enum class Difficulty(val speedMultiplier: Float) {
    EASY(0.8f),
    MEDIUM(1.3f),
    HARD(1.6f);

    companion object {
        fun fromLabel(label: String): Difficulty = when (label.lowercase()) {
            "easy" -> EASY
            "medium" -> MEDIUM
            "hard" -> HARD
            else -> MEDIUM
        }
    }
}

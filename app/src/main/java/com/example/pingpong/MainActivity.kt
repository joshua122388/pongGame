package com.example.pingpong

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.pingpong.ui.PongGameScreen
import com.example.pingpong.ui.theme.PingPongTheme
import android.util.Log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Registrar cualquier excepción no capturada para poder ver por qué la app podría cerrarse inmediatamente
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            Log.e("PongGame", "Uncaught exception in thread ${t.name}", e)
        }

        try {
            enableEdgeToEdge()
            setContent {
                PingPongTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        PongGameScreen(
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        } catch (t: Throwable) {
            Log.e("PongGame", "Exception during setContent", t)
            // Si algo fatal ocurre durante la configuración de composición, relanzar para mostrar el diálogo del sistema
            throw t
        }
    }
}

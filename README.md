### Proyecto

# Ping Pong - Juego Clásico de Pong

Proyecto de desarrollo de videojuego clásico Pong implementado en Kotlin usando Jetpack Compose para Android.

## Integrante

- **Joshua Contreras Rodriguez**

## Descripción del Proyecto

Este proyecto es una implementación moderna del clásico juego Pong (1972). El jugador controla la paleta izquierda mediante gestos táctiles, mientras que la paleta derecha es controlada por la CPU con tres niveles de dificultad. El objetivo es evitar que la pelota pase la paleta, cada vez que esto sucede, el oponente anota un punto.

### Características principales:

- **Interfaz de usuario moderna** construida con Jetpack Compose
- **Tres niveles de dificultad**: Fácil, Medio y Difícil
- **CPU de oponente** que ajusta su velocidad, tiempo de reacción y precisión según la dificultad
- **Controles táctiles** intuitivos para el jugador
- **Sistema de puntuación en tiempo real** que se actualiza automáticamente cuando cualquier jugador anota
- **Botón de salida durante el juego** para regresar al menú principal en cualquier momento
- **Física de juego realista** con detección de colisiones y efectos de rebote
- **Indicador visual de gol** con cambio de color de la pelota cuando se anota un punto

## Arquitectura de Software

El proyecto sigue la arquitectura **MVC (Model-View-Controller) adaptado para desarrollo de videojuegos**, con clara separación de responsabilidades:

```
com.example.pingpong/
├── model/              # Modelos de datos del dominio
│   └── Difficulty.kt   # Enum de niveles de dificultad
│
├── game/               # Lógica del juego (Controller)
│   └── GameModels.kt   # GameState, Ball, Paddle, física y IA de CPU
│
├── ui/                 # Capa de presentación (View)
│   ├── PongGameScreen.kt           # Pantalla principal del juego
│   ├── components/                 # Componentes UI reutilizables
│   │   ├── Branding.kt
│   │   ├── DifficultySelector.kt
│   │   ├── PlayerLabels.kt
│   │   └── Scoreboard.kt
│   └── theme/                      # Configuración de tema
│       ├── Color.kt
│       ├── Theme.kt
│       ├── Type.kt
│       └── PixelFonts.kt
│
└── MainActivity.kt     # Punto de entrada de la aplicación
```

### Justificación de la arquitectura:

Descripcion de los componentes de la arquitectura MVC:

- **Model**: Representación de los datos del juego (dificultad, estado de objetos)
- **View**: Componentes UI que renderizan el estado del juego
- **Controller**: `GameState` que maneja la lógica del juego, física, colisiones e IA de la CPU

Esta separación permite:
- Testabilidad de la lógica del juego independiente de la UI
- Reutilización de componentes UI
- Fácil modificación de reglas del juego sin afectar la presentación
- Bajo acoplamiento entre capas

## Cómo Jugar

1. Al iniciar, selecciona un nivel de dificultad:
   - **Fácil**: CPU más lenta y menos precisa
   - **Medio**: CPU balanceada
   - **Difícil**: CPU rápida y muy precisa

2. Toca el botón **"Iniciar"**

3. Controla la paleta izquierda deslizando tu dedo hacia arriba o abajo en la mitad izquierda de la pantalla

4. Evita que la pelota pase tu paleta. Cada fallo del oponente suma un punto en tu marcador (P1)

5. El juego continúa indefinidamente - ¡compite por el puntaje más alto!

## Cómo Ejecutar el Proyecto

### Requisitos previos:

- **Android Studio** Hedgehog (2023.1.1) o superior
- **JDK** 17 o superior
- **Android SDK** con API Level 34 (Android 14)
- Dispositivo Android o emulador con API Level 24+ (Android 7.0+)

### Pasos de instalación:

1. **Descomprime el archivo .zip** del proyecto

2. **Abre Android Studio** y selecciona `File > Open`

3. **Navega** a la carpeta descomprimida `ping_pong` y ábrela

4. **Espera** a que Gradle sincronice las dependencias automáticamente
   - Si no inicia automáticamente, haz clic en `File > Sync Project with Gradle Files`

5. **Conecta un dispositivo Android**  o **inicia un emulador**

6. **Ejecuta la aplicación**:
   - Haz clic en el botón con el icono de 'play' (en la parte de arriba de Android Studio)
   - O presiona `Shift + F10`

### Solución de problemas:

- **Gradle sync failed**: Verifica tu conexión a internet y que tienes instalado JDK 17
- **SDK not found**: Abre `Tools > SDK Manager` y descarga Android SDK API 34
- **App crashes**: Verifica que el dispositivo/emulador tenga al menos API Level 24

## Tecnologías Utilizadas

- **Lenguaje**: Kotlin 2.0.21
- **Framework UI**: Jetpack Compose (Material 3)
- **Compose BOM**: 2024.09.00
- **Build System**: Gradle (Kotlin DSL)
- **Plataforma**: Android (Min SDK 24, Target SDK 36)

### Dependencias principales:

```kotlin
- androidx.compose.material3
- androidx.compose.ui
- androidx.activity-compose
- androidx.lifecycle.runtime.ktx
 (Android KTX extensions)
- kotlinx.coroutines
```

## Evaluación Técnica

### a. UI
- Pantalla principal de juego con Canvas personalizado
- Componentes reutilizables (Scoreboard, DifficultySelector, etc.)
- Sistema de theming completo con Material 3
- Fuente personalizada pixel art
- Paleta de colores coherente

### b. Servicios
- No aplica para este proyecto (juego local sin APIs ni bases de datos)
- La lógica de  IA del CPU puede considerarse como "servicio" interno

### c. Arquitectura
- Separación clara de responsabilidades (MVC)
- Código modular y mantenible
- Lógica de negocio aislada en `game/`
- UI desacoplada de la lógica del juego

## Licencia

Proyecto académico - Universidad Latina de Costa Rica

---

**Desarrollado por Joshua Contreras Rodriguez**  
*Proyecto de Programación Móvil - 2025*

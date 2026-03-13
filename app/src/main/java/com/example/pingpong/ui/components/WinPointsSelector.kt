package com.example.pingpong.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.example.pingpong.ui.theme.PixelFontFamily

/**
 * Campo numérico para que el jugador ingrese cuántos puntos se necesitan para ganar.
 *
 * Validaciones:
 *  - No puede estar vacío
 *  - Debe ser mayor que 0
 *  - Máximo 99 puntos
 */
@Composable
fun WinPointsSelector(
    selected: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onValidityChange: (Boolean) -> Unit = {}
) {
    var rawInput by remember(selected) { mutableStateOf(if (selected > 0) selected.toString() else "") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    fun validate(text: String): Int? {
        if (text.isBlank()) {
            errorMsg = "Ingresa cuántos puntos quieres llegar"
            onValidityChange(false)
            return null
        }
        val n = text.trim().toIntOrNull()
        return when {
            n == null -> { errorMsg = "Solo se permiten números"; onValidityChange(false); null }
            n <= 0    -> { errorMsg = "Debe ser mayor que 0";     onValidityChange(false); null }
            n > 99    -> { errorMsg = "Máximo 99 puntos";         onValidityChange(false); null }
            else      -> { errorMsg = null;                        onValidityChange(true);  n }
        }
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            value = rawInput,
            onValueChange = { input ->
                // Solo permitir dígitos y máximo 2 caracteres
                if (input.length <= 2 && input.all { it.isDigit() }) {
                    rawInput = input
                    validate(input)?.let { onSelected(it) }
                }
            },
            label = {
                Text(
                    text = "Puntos para ganar",
                    fontFamily = PixelFontFamily,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            },
            isError = errorMsg != null,
            supportingText = {
                errorMsg?.let {
                    Text(text = it, fontFamily = PixelFontFamily, fontSize = 12.sp, color = Color(0xFFFF6B6B))
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    validate(rawInput)?.let { onSelected(it) }
                    focusManager.clearFocus()
                }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                errorBorderColor = Color(0xFFFF6B6B)
            ),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = PixelFontFamily,
                fontSize = 22.sp,
                color = Color.White
            ),
            modifier = Modifier.fillMaxWidth(0.5f)
        )
    }
}


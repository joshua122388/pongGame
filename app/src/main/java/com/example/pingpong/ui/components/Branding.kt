package com.example.pingpong.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.example.pingpong.ui.theme.PixelFontFamily

@Composable
fun TopRightBranding(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.TopEnd) {
        Text(
            text = "Proyecto Ulatina",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(8.dp),
            fontFamily = PixelFontFamily
        )
    }
}

@Composable
fun BottomCenterBranding(
    modifier: Modifier = Modifier,
    fontSizeSp: Int = 18,
    letterSpacingSp: Float = 1.25f,
    bottomPadding: Dp = 36.dp
) {
    Box(modifier = modifier, contentAlignment = Alignment.BottomCenter) {
        Text(
            text = "Proyecto Ulatina",
            color = Color.White,
            fontSize = fontSizeSp.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = letterSpacingSp.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = bottomPadding),
            textAlign = TextAlign.Center,
            fontFamily = PixelFontFamily
        )
    }
}

package com.mobileide.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobileide.app.ui.theme.StatusBarBackground

@Composable
fun StatusBar(line: Int, column: Int, language: String, fileName: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(StatusBarBackground)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = fileName ?: "nenhum arquivo aberto",
            color = Color.White,
            fontSize = 12.sp
        )
        Text(
            text = "Ln $line, Col $column   $language",
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

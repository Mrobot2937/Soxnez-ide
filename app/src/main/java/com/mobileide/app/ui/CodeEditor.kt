package com.mobileide.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobileide.app.ui.theme.EditorBackground
import com.mobileide.app.ui.theme.LineNumberColor
import com.mobileide.app.ui.theme.TextPrimary

/**
 * Editor de código com numeração de linha e highlighting de sintaxe.
 * onCursorMoved é usado para atualizar a barra de status (linha/coluna).
 */
@Composable
fun CodeEditor(
    value: TextFieldValue,
    language: String,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier
) {
    val lineCount = value.text.count { it == '\n' } + 1
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .background(EditorBackground)
            .fillMaxSize()
    ) {
        // Coluna de números de linha
        Column(
            modifier = Modifier
                .verticalScroll(scrollState, enabled = false)
                .padding(start = 8.dp, end = 8.dp, top = 8.dp)
        ) {
            for (i in 1..lineCount) {
                Text(
                    text = i.toString(),
                    color = LineNumberColor,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 19.sp
                )
            }
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                color = TextPrimary,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 19.sp
            ),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(TextPrimary),
            visualTransformation = { text ->
                androidx.compose.ui.text.input.TransformedText(
                    highlightCode(text.text, language),
                    androidx.compose.ui.text.input.OffsetMapping.Identity
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = 8.dp, end = 12.dp)
        )
    }
}

/** Calcula linha e coluna atuais a partir da posição do cursor, para a barra de status. */
fun lineAndColumnOf(text: String, cursorOffset: Int): Pair<Int, Int> {
    val safeOffset = cursorOffset.coerceIn(0, text.length)
    val textBeforeCursor = text.substring(0, safeOffset)
    val line = textBeforeCursor.count { it == '\n' } + 1
    val lastNewline = textBeforeCursor.lastIndexOf('\n')
    val column = safeOffset - lastNewline
    return line to column
}

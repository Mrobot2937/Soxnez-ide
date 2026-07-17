package com.mobileide.app.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.mobileide.app.ui.theme.SyntaxComment
import com.mobileide.app.ui.theme.SyntaxKeyword
import com.mobileide.app.ui.theme.SyntaxNumber
import com.mobileide.app.ui.theme.SyntaxString
import com.mobileide.app.ui.theme.SyntaxType
import com.mobileide.app.ui.theme.TextPrimary

private val keywordsByLanguage = mapOf(
    "python" to setOf(
        "def", "class", "import", "from", "return", "if", "elif", "else", "for", "while",
        "in", "is", "not", "and", "or", "try", "except", "finally", "with", "as", "pass",
        "break", "continue", "lambda", "None", "True", "False", "self", "yield", "async", "await"
    ),
    "javascript" to setOf(
        "function", "const", "let", "var", "return", "if", "else", "for", "while", "class",
        "extends", "import", "export", "default", "new", "this", "async", "await", "try",
        "catch", "finally", "typeof", "null", "undefined", "true", "false"
    ),
    "java" to setOf(
        "public", "private", "protected", "class", "interface", "extends", "implements",
        "static", "final", "void", "int", "String", "boolean", "return", "if", "else",
        "for", "while", "new", "try", "catch", "finally", "import", "package", "this", "null"
    ),
    "kotlin" to setOf(
        "fun", "val", "var", "class", "object", "interface", "override", "return", "if",
        "else", "for", "while", "when", "import", "package", "null", "true", "false",
        "private", "public", "internal", "companion", "suspend", "is", "as", "in"
    )
)

private val defaultKeywords = keywordsByLanguage.values.flatten().toSet()

fun highlightCode(code: String, language: String): AnnotatedString {
    val keywords = keywordsByLanguage[language] ?: defaultKeywords

    return buildAnnotatedString {
        append(code)

        // Strings ("...", '...')
        Regex("\"[^\"\\n]*\"|'[^'\\n]*'").findAll(code).forEach { m ->
            addStyle(SpanStyle(color = SyntaxString), m.range.first, m.range.last + 1)
        }

        // Comentários (// linha, # linha)
        Regex("//.*|#.*").findAll(code).forEach { m ->
            addStyle(SpanStyle(color = SyntaxComment), m.range.first, m.range.last + 1)
        }

        // Números
        Regex("\\b\\d+(\\.\\d+)?\\b").findAll(code).forEach { m ->
            addStyle(SpanStyle(color = SyntaxNumber), m.range.first, m.range.last + 1)
        }

        // Palavras-chave
        Regex("\\b(${keywords.joinToString("|")})\\b").findAll(code).forEach { m ->
            addStyle(SpanStyle(color = SyntaxKeyword), m.range.first, m.range.last + 1)
        }

        // Tipos/Classes (heurística: palavra iniciando com maiúscula)
        Regex("\\b[A-Z][A-Za-z0-9_]*\\b").findAll(code).forEach { m ->
            addStyle(SpanStyle(color = SyntaxType), m.range.first, m.range.last + 1)
        }
    }
}

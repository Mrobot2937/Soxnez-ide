package com.mobileide.app.model

import java.io.File

data class FileNode(
    val file: File,
    val name: String = file.name,
    val isDirectory: Boolean = file.isDirectory,
    var isExpanded: Boolean = false,
    val depth: Int = 0
) {
    fun children(): List<FileNode> {
        if (!isDirectory) return emptyList()
        return (file.listFiles()?.toList() ?: emptyList())
            .sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
            .map { FileNode(it, depth = depth + 1) }
    }
}

data class OpenTab(
    val displayName: String,
    val extension: String,
    var content: String,
    var isDirty: Boolean = false,
    val localFile: File? = null,       // preenchido quando o arquivo é do armazenamento interno do app
    val safUri: android.net.Uri? = null // preenchido quando o arquivo foi aberto via SAF (pasta escolhida pelo usuário)
) {
    /** Chave estável para identificar a aba, seja qual for a origem do arquivo. */
    val key: String
        get() = safUri?.toString() ?: localFile?.absolutePath ?: displayName

    val language: String
        get() = when (extension.lowercase()) {
            "py" -> "python"
            "js", "jsx" -> "javascript"
            "ts", "tsx" -> "typescript"
            "java" -> "java"
            "kt", "kts" -> "kotlin"
            "html" -> "html"
            "css" -> "css"
            "json" -> "json"
            "md" -> "markdown"
            "xml" -> "xml"
            "c", "h" -> "c"
            "cpp", "hpp" -> "cpp"
            else -> "plaintext"
        }

    companion object {
        fun fromLocalFile(file: File): OpenTab = OpenTab(
            displayName = file.name,
            extension = file.extension,
            content = file.readText(),
            localFile = file
        )
    }
}

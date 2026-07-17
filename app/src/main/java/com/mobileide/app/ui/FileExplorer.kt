package com.mobileide.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobileide.app.model.FileNode
import com.mobileide.app.ui.theme.SidebarBackground
import com.mobileide.app.ui.theme.TextPrimary
import java.io.File

/**
 * Explorador de arquivos recursivo. Mantém um mapa de pastas expandidas
 * para renderizar a árvore de forma achatada (flatten) em uma LazyColumn.
 */
@Composable
fun FileExplorer(
    rootFile: File,
    onFileClick: (File) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedPaths by remember { mutableStateOf(setOf(rootFile.absolutePath)) }

    val flatNodes = remember(rootFile, expandedPaths) {
        buildFlatTree(rootFile, expandedPaths, depth = 0)
    }

    Column(
        modifier = modifier
            .background(SidebarBackground)
            .fillMaxHeight()
    ) {
        Text(
            text = "EXPLORADOR",
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(12.dp)
        )
        LazyColumn {
            items(flatNodes) { node ->
                FileRow(
                    node = node,
                    isExpanded = expandedPaths.contains(node.file.absolutePath),
                    onClick = {
                        if (node.isDirectory) {
                            expandedPaths = if (expandedPaths.contains(node.file.absolutePath)) {
                                expandedPaths - node.file.absolutePath
                            } else {
                                expandedPaths + node.file.absolutePath
                            }
                        } else {
                            onFileClick(node.file)
                        }
                    }
                )
            }
        }
    }
}

private fun buildFlatTree(root: File, expanded: Set<String>, depth: Int): List<FileNode> {
    val result = mutableListOf<FileNode>()
    val children = (root.listFiles()?.toList() ?: emptyList())
        .sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))

    for (child in children) {
        val node = FileNode(child, depth = depth)
        result.add(node)
        if (child.isDirectory && expanded.contains(child.absolutePath)) {
            result.addAll(buildFlatTree(child, expanded, depth + 1))
        }
    }
    return result
}

@Composable
private fun FileRow(node: FileNode, isExpanded: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = (12 + node.depth * 14).dp, top = 6.dp, bottom = 6.dp, end = 8.dp)
    ) {
        Icon(
            imageVector = when {
                node.isDirectory && isExpanded -> Icons.Filled.FolderOpen
                node.isDirectory -> Icons.Filled.Folder
                else -> Icons.Filled.Description
            },
            contentDescription = null,
            tint = if (node.isDirectory) Color(0xFFDCB67A) else Color(0xFF8FBADC),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = node.name, color = TextPrimary, fontSize = 13.sp)
    }
}

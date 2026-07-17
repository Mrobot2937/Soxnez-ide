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
import androidx.documentfile.provider.DocumentFile
import com.mobileide.app.ui.theme.SidebarBackground
import com.mobileide.app.ui.theme.TextPrimary

private data class DocNode(val doc: DocumentFile, val depth: Int)

@Composable
fun FileExplorerSaf(
    root: DocumentFile,
    onFileClick: (DocumentFile) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedUris by remember { mutableStateOf(setOf(root.uri.toString())) }

    val flatNodes = remember(root, expandedUris) {
        buildFlatTree(root, expandedUris, depth = 0)
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
                DocRow(
                    node = node,
                    isExpanded = expandedUris.contains(node.doc.uri.toString()),
                    onClick = {
                        if (node.doc.isDirectory) {
                            val key = node.doc.uri.toString()
                            expandedUris = if (expandedUris.contains(key)) {
                                expandedUris - key
                            } else {
                                expandedUris + key
                            }
                        } else {
                            onFileClick(node.doc)
                        }
                    }
                )
            }
        }
    }
}

private fun buildFlatTree(root: DocumentFile, expanded: Set<String>, depth: Int): List<DocNode> {
    val result = mutableListOf<DocNode>()
    val children = (root.listFiles().toList())
        .sortedWith(compareBy({ !it.isDirectory }, { (it.name ?: "").lowercase() }))

    for (child in children) {
        result.add(DocNode(child, depth))
        if (child.isDirectory && expanded.contains(child.uri.toString())) {
            result.addAll(buildFlatTree(child, expanded, depth + 1))
        }
    }
    return result
}

@Composable
private fun DocRow(node: DocNode, isExpanded: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = (12 + node.depth * 14).dp, top = 6.dp, bottom = 6.dp, end = 8.dp)
    ) {
        Icon(
            imageVector = when {
                node.doc.isDirectory && isExpanded -> Icons.Filled.FolderOpen
                node.doc.isDirectory -> Icons.Filled.Folder
                else -> Icons.Filled.Description
            },
            contentDescription = null,
            tint = if (node.doc.isDirectory) Color(0xFFDCB67A) else Color(0xFF8FBADC),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = node.doc.name ?: "?", color = TextPrimary, fontSize = 13.sp)
    }
}

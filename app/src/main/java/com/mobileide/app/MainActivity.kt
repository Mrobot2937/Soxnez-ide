package com.mobileide.app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import com.mobileide.app.model.OpenTab
import com.mobileide.app.python.PythonRunner
import com.mobileide.app.ui.CodeEditor
import com.mobileide.app.ui.ConsolePanel
import com.mobileide.app.ui.FileExplorer
import com.mobileide.app.ui.FileExplorerSaf
import com.mobileide.app.ui.StatusBar
import com.mobileide.app.ui.TopTabBar
import com.mobileide.app.ui.lineAndColumnOf
import com.mobileide.app.ui.theme.ActivityBarBackground
import com.mobileide.app.ui.theme.IconInactive
import com.mobileide.app.ui.theme.MobileIDETheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobileIDETheme {
                IDEApp(internalRoot = filesDir)
            }
        }
    }
}

@Composable
fun IDEApp(internalRoot: File) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var documentRoot by remember { mutableStateOf<DocumentFile?>(null) }
    var openTabs by remember { mutableStateOf(listOf<OpenTab>()) }
    var activeTab by remember { mutableStateOf<OpenTab?>(null) }
    var editorValue by remember { mutableStateOf(TextFieldValue("")) }
    var showExplorer by remember { mutableStateOf(true) }
    var consoleOutput by remember { mutableStateOf<String?>(null) }
    var isRunning by remember { mutableStateOf(false) }

    // Seletor de pasta real do celular (Storage Access Framework)
    val openFolderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            documentRoot = DocumentFile.fromTreeUri(context, uri)
        }
    }

    // Garante um arquivo de exemplo na primeira abertura (armazenamento interno)
    LaunchedEffect(Unit) {
        val sample = File(internalRoot, "main.py")
        if (!sample.exists()) {
            sample.writeText(
                "# Bem-vindo ao Soxnez\n" +
                "def saudacao(nome):\n" +
                "    return f\"Ola, {nome}!\"\n\n" +
                "print(saudacao(\"Mundo\"))\n"
            )
        }
    }

    fun openTabIfNeeded(newTab: OpenTab) {
        val existing = openTabs.find { it.key == newTab.key }
        val tab = existing ?: newTab.also { openTabs = openTabs + it }
        activeTab = tab
        editorValue = TextFieldValue(tab.content)
    }

    fun openLocalFile(file: File) {
        openTabIfNeeded(OpenTab.fromLocalFile(file))
    }

    fun openSafFile(doc: DocumentFile) {
        val text = context.contentResolver.openInputStream(doc.uri)?.bufferedReader()?.use { it.readText() } ?: ""
        openTabIfNeeded(
            OpenTab(
                displayName = doc.name ?: "arquivo",
                extension = doc.name?.substringAfterLast('.', "") ?: "",
                content = text,
                safUri = doc.uri
            )
        )
    }

    fun saveActiveTab() {
        val tab = activeTab ?: return
        when {
            tab.localFile != null -> tab.localFile.writeText(editorValue.text)
            tab.safUri != null -> {
                context.contentResolver.openOutputStream(tab.safUri, "wt")?.use {
                    it.write(editorValue.text.toByteArray())
                }
            }
        }
        tab.content = editorValue.text
        tab.isDirty = false
        openTabs = openTabs.toList()
    }

    fun runActiveTab() {
        val tab = activeTab ?: return
        if (tab.language != "python") {
            consoleOutput = "Execução ainda só é suportada para arquivos Python (.py).\n" +
                "Linguagem atual: ${tab.language}"
            return
        }
        isRunning = true
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching { PythonRunner.runCode(context, editorValue.text) }
                    .getOrElse { "Erro ao iniciar o interpretador Python:\n${it.message}" }
            }
            consoleOutput = result
            isRunning = false
        }
    }

    val (line, column) = lineAndColumnOf(editorValue.text, editorValue.selection.start)

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ActivityBarBackground)
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    IconButton(onClick = { showExplorer = !showExplorer }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Mostrar/ocultar explorador", tint = IconInactive)
                    }
                    IconButton(onClick = { openFolderLauncher.launch(null) }) {
                        Icon(Icons.Filled.CreateNewFolder, contentDescription = "Abrir pasta do celular", tint = IconInactive)
                    }
                }
                Row {
                    IconButton(onClick = { saveActiveTab() }) {
                        Icon(Icons.Filled.Save, contentDescription = "Salvar", tint = IconInactive)
                    }
                    IconButton(onClick = { runActiveTab() }, enabled = !isRunning) {
                        Icon(
                            Icons.Filled.PlayArrow,
                            contentDescription = "Executar",
                            tint = if (isRunning) IconInactive else Color(0xFF4EC9B0)
                        )
                    }
                }
            }
        },
        bottomBar = {
            Column {
                consoleOutput?.let { output ->
                    ConsolePanel(output = output, onClose = { consoleOutput = null })
                }
                StatusBar(
                    line = line,
                    column = column,
                    language = activeTab?.language ?: "plaintext",
                    fileName = activeTab?.displayName
                )
            }
        }
    ) { padding ->
        Row(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (showExplorer) {
                if (documentRoot != null) {
                    FileExplorerSaf(
                        root = documentRoot!!,
                        onFileClick = { openSafFile(it) },
                        modifier = Modifier.width(200.dp)
                    )
                } else {
                    FileExplorer(
                        rootFile = internalRoot,
                        onFileClick = { openLocalFile(it) },
                        modifier = Modifier.width(200.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                TopTabBar(
                    tabs = openTabs,
                    activeTab = activeTab,
                    onTabSelected = { tab ->
                        activeTab = tab
                        editorValue = TextFieldValue(tab.content)
                    },
                    onTabClosed = { tab ->
                        openTabs = openTabs - tab
                        if (activeTab == tab) {
                            activeTab = openTabs.lastOrNull()
                            editorValue = TextFieldValue(activeTab?.content ?: "")
                        }
                    }
                )

                if (activeTab != null) {
                    CodeEditor(
                        value = editorValue,
                        language = activeTab!!.language,
                        onValueChange = {
                            editorValue = it
                            activeTab?.isDirty = true
                        },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Box(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

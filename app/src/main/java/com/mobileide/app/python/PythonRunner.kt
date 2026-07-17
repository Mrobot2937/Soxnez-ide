package com.mobileide.app.python

import android.content.Context
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

/**
 * Executa código Python localmente no dispositivo usando Chaquopy.
 * Só funciona para arquivos .py — outras linguagens ainda não têm
 * execução real implementada (ver README).
 */
object PythonRunner {
    fun runCode(context: Context, code: String): String {
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(context))
        }
        val py = Python.getInstance()
        val runner = py.getModule("runner")
        return runner.callAttr("run", code).toString()
    }
}

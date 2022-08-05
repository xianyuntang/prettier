package com.github.xianyuntang.prettier.actions

import com.intellij.codeInsight.hint.HintManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.io.isFile
import java.io.BufferedReader
import java.nio.file.Path
import java.nio.file.Paths


internal class PrettierAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val file: VirtualFile? = e.getData(PlatformDataKeys.VIRTUAL_FILE)
        val project: Project? = e.getData(PlatformDataKeys.PROJECT)
        if (file !== null && project !== null) {
            val prettierPath = this.findPrettier(Paths.get(file.path), Paths.get(project.basePath!!))
            if (prettierPath === null) {
                CommonDataKeys.EDITOR.getData(e.dataContext)
                    ?.let { HintManager.getInstance().showErrorHint(it, "Prettier Not Found") };
            }
            val process = Runtime.getRuntime().exec("$prettierPath ${file.path}")
            val prettierOutput = readProcessOutput(process)
            val document = FileDocumentManager.getInstance().getDocument(file)
            if (document !== null) {
                ApplicationManager.getApplication().runWriteAction {
                    document.setText(
                        prettierOutput
                    )
                }
            }
        }
    }

    private fun findPrettier(currentPath: Path, stopPath: Path): String? {
        val path = Paths.get(currentPath.normalize().toString(), "node_modules", ".bin", "prettier.cmd")
        if (path.isFile()) {
            return path.normalize().toString()
        } else {
            if (stopPath == currentPath) {
                return null
            }
            return this.findPrettier(currentPath.parent, stopPath)
        }
    }
    private fun readProcessOutput(process: Process): String {
        val bufferedReader: BufferedReader = process.inputStream.bufferedReader()
        val iterator = bufferedReader.lineSequence().iterator()
        var line = ""
        while (iterator.hasNext()) {
            line += iterator.next()
            line += "\n"
        }
        return line

    }

}
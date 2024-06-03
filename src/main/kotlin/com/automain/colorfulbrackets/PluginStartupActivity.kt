package com.automain.colorfulbrackets

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.psi.PsiDocumentManager

//项目启动时执行
class PluginStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        val updater = project.service<BracketUpdater>()
        updater.register()
        val connection = project.messageBus.connect()
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, project.service<FileOpenListener>())
        ApplicationManager.getApplication().invokeLater {
            val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return@invokeLater
            val document = editor.document
            val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return@invokeLater
            println("projectStart:::::")
            BracketFinder.findBrackets( psiFile, editor)
        }
    }
}

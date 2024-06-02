package com.automain.colorfulbrackets

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.psi.PsiDocumentListener
import java.awt.event.WindowFocusListener

class PluginStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        val updater = project.service<BracketUpdater>()
        updater.register()
        val connection = project.messageBus.connect()
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, project.service<FileOpenListener>())
    }
}

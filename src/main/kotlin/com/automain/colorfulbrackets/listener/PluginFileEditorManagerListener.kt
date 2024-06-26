﻿package com.automain.colorfulbrackets.listener

import com.automain.colorfulbrackets.BracketFinder
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.TextEditor

//文件打开，选择的文件变化
@Service(Service.Level.PROJECT)
class PluginFileEditorManagerListener(private val project: Project) : FileEditorManagerListener {

    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        val fileEditor = source.getSelectedEditor(file) as? TextEditor ?: return
        val editor: Editor = fileEditor.editor
        val document = editor.document
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return
        BracketFinder.setJumpNeedClean()
        BracketFinder.findBrackets(psiFile, editor)
    }

    override fun selectionChanged(event: FileEditorManagerEvent) {
        val fileEditor = event.newEditor as? TextEditor ?: return
        //val fileEditor = source.getSelectedEditor(event.newEditor?.file) as? TextEditor ?: return
        val editor: Editor = fileEditor.editor
        val document = editor.document 
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return
        BracketFinder.setJumpNeedClean()
        BracketFinder.findBrackets(psiFile, editor)
    }

    fun register() {
        val connection = project.messageBus.connect()
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, project.service<PluginFileEditorManagerListener>())
    }
}

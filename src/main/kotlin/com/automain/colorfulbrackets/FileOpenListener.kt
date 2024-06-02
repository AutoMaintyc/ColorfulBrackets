package com.automain.colorfulbrackets

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.TextEditor

@Service(Service.Level.PROJECT)
class FileOpenListener(private val project: Project) : FileEditorManagerListener {

    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        val fileEditor = source.getSelectedEditor(file) as? TextEditor ?: return
        val editor: Editor = fileEditor.editor
        val document = editor.document
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return

        BracketFinder.findBrackets( psiFile, editor)
    }
}

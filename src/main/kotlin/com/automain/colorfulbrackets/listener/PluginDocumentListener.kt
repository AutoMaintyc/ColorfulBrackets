package com.automain.colorfulbrackets.listener

//import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
//import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
//import com.intellij.psi.PsiDocumentManager

//文档修改
@Service(Service.Level.PROJECT)
class PluginDocumentListener(private val project: Project) : DocumentListener {

    override fun documentChanged(event: DocumentEvent) {
//        ApplicationManager.getApplication().invokeLater {
//            val document: Document = event.document
//            val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return@invokeLater
//            val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return@invokeLater
//            //println("documentChanged::::: ${event.document}")
//            BracketFinder.findBrackets( psiFile, editor)
//        }
    }

    fun register() {
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return
        val document = editor.document
        document.addDocumentListener(this)
    }
}

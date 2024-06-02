package com.automain.colorfulbrackets

import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.PsiDocumentTransactionListener

class PluginPsiDocumentTransactionListener : PsiDocumentTransactionListener {
    override fun transactionStarted(doc: Document, file: PsiFile) {
        // 在这里添加开始事务时的逻辑
        println("transactionStarted::::: $doc")
    }

    override fun transactionCompleted(doc: Document, file: PsiFile) {
        // 在这里添加事务完成后的逻辑
        val project = file.project
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return
        println("transactionCompleted::::: $doc")
        BracketFinder.findBrackets( file, editor)
    }
}

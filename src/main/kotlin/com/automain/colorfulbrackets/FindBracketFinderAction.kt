package com.automain.colorfulbrackets

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager

class FindBracketsAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return
        val document = editor.document
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return

        BracketFinder.findBrackets(project, psiFile, editor)
    }
}

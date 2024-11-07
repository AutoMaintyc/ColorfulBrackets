package com.automain.colorfulbrackets.action

import com.automain.colorfulbrackets.BracketFinder
import com.intellij.codeInsight.highlighting.HighlightManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager

//右键的
class FindBracketsAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return
        val document = editor.document
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return

        BracketFinder.findBrackets(psiFile, editor)
    }
}

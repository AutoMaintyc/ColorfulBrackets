package com.automain.colorfulbrackets

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiTreeChangeEvent
import com.intellij.psi.PsiTreeChangeListener

//PSI发生变化
@Service(Service.Level.PROJECT)
class PluginPsiTreeChangeListener(private val project: Project) : PsiTreeChangeListener {
    override fun beforeChildAddition(event: PsiTreeChangeEvent) {

        val psiFile = event.file as PsiFile
        val editor = FileEditorManager.getInstance(project).selectedTextEditor as Editor
        BracketFinder.findBrackets( psiFile, editor)
    }

    override fun beforeChildRemoval(event: PsiTreeChangeEvent) {
        TODO("Not yet implemented")
    }

    override fun beforeChildReplacement(event: PsiTreeChangeEvent) {
        TODO("Not yet implemented")
    }

    override fun beforeChildMovement(event: PsiTreeChangeEvent) {
        TODO("Not yet implemented")
    }

    override fun beforeChildrenChange(event: PsiTreeChangeEvent) {
        val psiFile = event.file as PsiFile
        val editor = FileEditorManager.getInstance(project).selectedTextEditor as Editor
        BracketFinder.findBrackets( psiFile, editor)
    }

    override fun beforePropertyChange(event: PsiTreeChangeEvent) {
        TODO("Not yet implemented")
    }

    override fun childAdded(event: PsiTreeChangeEvent) {
    }

    override fun childRemoved(event: PsiTreeChangeEvent) {
        TODO("Not yet implemented")
    }

    override fun childReplaced(event: PsiTreeChangeEvent) {
        TODO("Not yet implemented")
    }

    override fun childrenChanged(event: PsiTreeChangeEvent) {
        TODO("Not yet implemented")
    }

    override fun childMoved(event: PsiTreeChangeEvent) {
        TODO("Not yet implemented")
    }

    override fun propertyChanged(event: PsiTreeChangeEvent) {
        TODO("Not yet implemented")
    }

    fun register() {
        PsiManager.getInstance(project).addPsiTreeChangeListener(this, Disposable { project })
    }
}


package com.automain.colorfulbrackets

//import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
//import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
//import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiTreeChangeEvent
import com.intellij.psi.PsiTreeChangeListener

//PSI发生变化
@Service(Service.Level.PROJECT)
class PluginPsiTreeChangeListener(private val project: Project) : PsiTreeChangeListener {
    override fun beforeChildAddition(event: PsiTreeChangeEvent) {
//        val psiFile = event.file as PsiFile
//        val editor = FileEditorManager.getInstance(project).selectedTextEditor as Editor
//        BracketFinder.findBrackets( psiFile, editor)
    }

    override fun beforeChildRemoval(event: PsiTreeChangeEvent) {
//        val psiFile = event.file as PsiFile
//        val editor = FileEditorManager.getInstance(project).selectedTextEditor as Editor
//        BracketFinder.findBrackets( psiFile, editor)
    }

    override fun beforeChildReplacement(event: PsiTreeChangeEvent) {
//        val psiFile = event.file as PsiFile
//        val editor = FileEditorManager.getInstance(project).selectedTextEditor as Editor
//        BracketFinder.findBrackets( psiFile, editor)
    }

    override fun beforeChildMovement(event: PsiTreeChangeEvent) {
//        val psiFile = event.file as PsiFile
//        val editor = FileEditorManager.getInstance(project).selectedTextEditor as Editor
//        BracketFinder.findBrackets( psiFile, editor)
    }

    override fun beforeChildrenChange(event: PsiTreeChangeEvent) {
//        val psiFile = event.file as PsiFile
//        val editor = FileEditorManager.getInstance(project).selectedTextEditor as Editor
//        BracketFinder.findBrackets( psiFile, editor)
    }

    override fun beforePropertyChange(event: PsiTreeChangeEvent) {
//        val psiFile = event.file as PsiFile
//        val editor = FileEditorManager.getInstance(project).selectedTextEditor as Editor
//        BracketFinder.findBrackets( psiFile, editor)
    }

    override fun childAdded(event: PsiTreeChangeEvent) {
        val psiFile = event.file?:return
        val editor = FileEditorManager.getInstance(project).selectedTextEditor?:return
        BracketFinder.findBrackets( psiFile, editor)
    }

    override fun childRemoved(event: PsiTreeChangeEvent) {
        val psiFile = event.file?:return
        val editor = FileEditorManager.getInstance(project).selectedTextEditor?:return
        BracketFinder.findBrackets( psiFile, editor)
    }

    override fun childReplaced(event: PsiTreeChangeEvent) {
        val psiFile = event.file?:return
        val editor = FileEditorManager.getInstance(project).selectedTextEditor?:return
        BracketFinder.findBrackets( psiFile, editor)
    }

    override fun childrenChanged(event: PsiTreeChangeEvent) {
        val psiFile = event.file?:return
        val editor = FileEditorManager.getInstance(project).selectedTextEditor?:return
        BracketFinder.findBrackets( psiFile, editor)
    }

    override fun childMoved(event: PsiTreeChangeEvent) {
        val psiFile = event.file?:return
        val editor = FileEditorManager.getInstance(project).selectedTextEditor?:return
        BracketFinder.findBrackets( psiFile, editor)
    }

    override fun propertyChanged(event: PsiTreeChangeEvent) {
        val psiFile = event.file?:return
        val editor = FileEditorManager.getInstance(project).selectedTextEditor?:return
        BracketFinder.findBrackets( psiFile, editor)
    }

    fun register() {
        PsiManager.getInstance(project).addPsiTreeChangeListener(this) { project }
    }
}


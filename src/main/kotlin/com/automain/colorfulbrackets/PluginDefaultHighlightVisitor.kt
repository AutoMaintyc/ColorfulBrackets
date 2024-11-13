package com.automain.colorfulbrackets
import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder
import com.intellij.ide.util.PropertiesComponent
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement

class PluginDefaultHighlightVisitor : HighlightVisitor {

    val action = Runnable {}

    override fun suitableForFile(file: PsiFile): Boolean {
        return PropertiesComponent.getInstance().getBoolean("{}")
    }
    override fun analyze(
        file: PsiFile,
        updateWholeFile: Boolean,
        holder: HighlightInfoHolder,
        action: Runnable
    ): Boolean {
        /*在这里进行高亮操作*/
        println("analyze")
        action.run()
        return true
    }

    override fun clone(): HighlightVisitor {
        println("clone")
        return this
    }

    override fun visit(element: PsiElement) {
        /*在这里找到需要高亮的*/
        println("Visiting: " + element.text)
        println("visit")
        val type = (element as? LeafPsiElement)?.elementType ?: return
        if (element is PsiErrorElement) {
            println(type)
        }
    }
}
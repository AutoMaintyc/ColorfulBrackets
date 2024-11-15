package com.automain.colorfulbrackets
import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder
import com.intellij.ide.util.PropertiesComponent
import com.intellij.lang.BracePair
import com.intellij.lang.LanguageBraceMatching
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.jetbrains.rider.languages.fileTypes.csharp.assists.CSharpBraceMatcher

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
        val vee = CSharpBraceMatcher()
        for(i in vee.pairs) {
            BracePair(i.leftBraceType,i.rightBraceType,true)
        }

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
        val leftBraceType = element as LeafPsiElement
        if (leftBraceType != null) {

            LanguageBraceMatching.INSTANCE.forLanguage(element.language).pairs
            println(type)
        }
    }

    /*队尾Add，队头AddFirst*/
    val arrayDeque = ArrayDeque<PsiElement>()
}
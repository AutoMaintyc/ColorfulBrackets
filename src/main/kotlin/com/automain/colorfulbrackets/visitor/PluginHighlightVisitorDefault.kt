﻿package com.automain.colorfulbrackets.visitor

import com.automain.colorfulbrackets.BracketHelper
import com.automain.colorfulbrackets.ColorHelper
import com.automain.colorfulbrackets.PluginSetting
import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

class PluginHighlightVisitorDefault : HighlightVisitor {
    private var highlightInfoHolder: HighlightInfoHolder? = null

    override fun suitableForFile(file: PsiFile): Boolean {
        return BracketHelper.pluginIsOpen()
    }

    override fun analyze(
        file: PsiFile,
        updateWholeFile: Boolean,
        holder: HighlightInfoHolder,
        action: Runnable
    ): Boolean {
        highlightInfoHolder = holder
        try { action.run() }
        catch (e: Throwable) { println(e.toString()) }
        finally { highlightInfoHolder = null; }
        return true
    }


    override fun clone(): HighlightVisitor {
        return this
    }

    override fun visit(element: PsiElement) {
        /*在这里找到需要高亮的*/
        if (!BracketHelper.needCheck(element)) return
        val rightBracket: PsiElement? = BracketHelper.findRightBracket(element)
        if (rightBracket != null) {
            val bracketKey = element.text + rightBracket.text
            if (PluginSetting.getBracketSetting(bracketKey)) {
                setHighlight(element)
                setHighlight(rightBracket)
            }
        }
    }

    /** 设置高亮信息 */
    private fun setHighlight(element: PsiElement){
        /*设置高亮信息*/
        val highlightInfo = ColorHelper.getHighlightInfo(element)
        highlightInfoHolder?.add(highlightInfo)
    }
}
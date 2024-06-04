package com.automain.colorfulbrackets

import com.intellij.openapi.editor.Editor
import java.util.*
import com.intellij.openapi.editor.markup.*
import com.intellij.psi.*
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import com.intellij.ui.JBColor
import com.intellij.openapi.editor.Document
import java.awt.Color
import kotlin.random.Random
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiComment
import com.intellij.psi.impl.source.tree.LeafPsiElement

object BracketFinder {

    fun findBrackets(file: PsiFile, editor: Editor) {

        val stack = Stack<PsiElement>()
        stack.clear()
        val pairs = mutableListOf<Pair<PsiElement, PsiElement>>()
        pairs.clear()
        val unmatchedOpenings = mutableListOf<PsiElement>()
        unmatchedOpenings.clear()
        val unmatchedClosings = mutableListOf<PsiElement>()
        unmatchedClosings.clear()
        file.accept(object : PsiRecursiveElementWalkingVisitor() {
            override fun visitElement(element: PsiElement) {
                //跳过注释||是否处于注释中
                if (element is PsiComment || element.parent is PsiComment)
                {
                    return
                }
                //字面量||是否处于字面量中
                if (element is PsiLiteralValue || element.parent is PsiLiteralValue|| element.parent.parent is PsiLiteralValue) 
                {
                    return
                }
                
                if (needRet(element)) return

                super.visitElement(element)
                 when (element.text) {
                    "{" -> {
                        stack.push(element)
                    }

                    "}" -> {
                        if (stack.isNotEmpty()) {
                            val openingBracket = stack.pop()
                            pairs.add(openingBracket to element)
                        } else {
                            unmatchedClosings.add(element)
                            println("Unmatched closing bracket at ${element.textRange}")
                        }
                    }
                }
            }
        })

        if (stack.isNotEmpty()) {
            println("Unmatched opening brackets:")
            stack.forEach {
                unmatchedOpenings.add(it)
                println("Opening bracket at ${it.textRange}")
            }
        }

        val markupModel = editor.markupModel
        val document = editor.document
        pairs.forEach { (open, close) ->
            val color = getRandomColor()
            highlightBracket(markupModel, open, color, document)
            highlightBracket(markupModel, close, color, document)
            println("Found bracket pair: (${open.textRange}, ${close.textRange})")
        }

        // Highlight unmatched opening brackets
        unmatchedOpenings.forEach {
            highlightBracket(
                markupModel,
                it,
                JBColor(Color.RED, Color.RED),
                document
            ) // You can choose a specific color for unmatched brackets
        }

        // Highlight unmatched closing brackets
        unmatchedClosings.forEach {
            highlightBracket(
                markupModel,
                it,
                JBColor(Color.RED, Color.RED),
                document
            ) // You can choose a specific color for unmatched brackets
        }
    }

    private fun highlightBracket(markupModel: MarkupModel, element: PsiElement, color: Color, document: Document) {
        val textAttributes = TextAttributes()

        textAttributes.foregroundColor = color
        if (element.startOffset >= 0 && element.endOffset <= document.textLength) {
            markupModel.addRangeHighlighter(
                element.textRange.startOffset,
                element.textRange.endOffset,
                HighlighterLayer.ADDITIONAL_SYNTAX,
                textAttributes,
                HighlighterTargetArea.EXACT_RANGE
            )
        }
    }

    private fun getRandomColor(): JBColor {
        val red = Random.nextInt(256)
        val green = Random.nextInt(256)
        val blue = Random.nextInt(256)
        return JBColor(Color(red, green, blue), Color(red, green, blue))
    }
    
    private fun needRet(element: PsiElement): Boolean{
        return (element::class.simpleName == "KtStringTemplateExpression"
                ||element::class.simpleName == "KtLiteralStringTemplateEntry"
                ||element::class.simpleName == "KtBlockStringTemplateEntry"
                ||((element is LeafPsiElement) && element.elementType::class.simpleName == "KtToken")
                )
    }
}

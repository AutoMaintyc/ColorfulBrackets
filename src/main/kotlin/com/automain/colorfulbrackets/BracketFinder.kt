package com.automain.colorfulbrackets

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementWalkingVisitor
import java.util.*

import com.intellij.openapi.editor.markup.*
import java.awt.Color
import kotlin.random.Random

object BracketFinder {
    fun findBrackets(project: Project, file: PsiFile, editor: Editor) {
        if (file is PsiFile) {
            val stack = Stack<PsiElement>()
            val pairs = mutableListOf<Pair<PsiElement, PsiElement>>()

            file.accept(object : PsiRecursiveElementWalkingVisitor() {
                override fun visitElement(element: PsiElement) {
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
                                println("Unmatched closing bracket at ${element.textRange}")
                            }
                        }
                    }
                }
            })

            if (stack.isNotEmpty()) {
                println("Unmatched opening brackets:")
                stack.forEach { println("Opening bracket at ${it.textRange}") }
            }

            pairs.forEach { (open, close) ->
                val markupModel = editor.markupModel
                val color = getRandomColor()
                highlightBracket(markupModel, open, color)
                highlightBracket(markupModel, close, color)
                println("Found bracket pair: (${open.textRange}, ${close.textRange})")
            }
        }
    }

    private fun highlightBracket(markupModel: MarkupModel, element: PsiElement,color: Color) {
        val textAttributes = TextAttributes()
        
        textAttributes.foregroundColor = color // 设置括号颜色
        val highlighter: RangeHighlighter = markupModel.addRangeHighlighter(
            element.textRange.startOffset,
            element.textRange.endOffset,
            HighlighterLayer.ADDITIONAL_SYNTAX,
            textAttributes,
            HighlighterTargetArea.EXACT_RANGE
        )
    }

    private fun getRandomColor(): Color {
        val red = Random.nextInt(256)
        val green = Random.nextInt(256)
        val blue = Random.nextInt(256)
        return Color(red, green, blue)
    }
}

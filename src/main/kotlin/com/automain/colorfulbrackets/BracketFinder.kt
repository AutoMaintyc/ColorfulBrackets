package com.automain.colorfulbrackets

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.*
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import com.intellij.ui.JBColor
import org.mozilla.javascript.ast.ParenthesizedExpression
import java.awt.Color
import java.util.*
import kotlin.random.Random

object BracketFinder {

    fun findBrackets(file: PsiFile, editor: Editor) {
        currentFile = file
        currentEditor = editor
        markupModel = currentEditor.markupModel
        document = currentEditor.document
        find()
    }

    fun find(){

        //清理掉原来的highlighters
        //markupModel.removeAllHighlighters()
        if (isNeedClean) {
            for (highlighter in highlighters) {
                markupModel.removeHighlighter(highlighter)
            }
        } else {
            highlighters.clear()
            isNeedClean = true
        }

        var pairs: MutableList<Pair<PsiElement, PsiElement>>
        if (PropertiesComponent.getInstance().getBoolean("{}")) {
            pairs = findElement( "{", "}")
            highlightBrackets(pairs)
        }
        if (PropertiesComponent.getInstance().getBoolean("<>")) {
            pairs = findElement( "<", ">")
            highlightBrackets(pairs)
        }
        if (PropertiesComponent.getInstance().getBoolean("[]")) {
            pairs = findElement( "[", "]")
            highlightBrackets(pairs)
        }
        if (PropertiesComponent.getInstance().getBoolean("()")) {
            pairs = findElement( "(", ")")
            highlightBrackets(pairs)
        }
    }

    //设置为 需要跳过 清除高亮 ,即不需要清除高亮
    fun setJumpNeedClean() {
        isNeedClean = false
    }

    private fun findElement(
        leftElement: String,
        rightElement: String
    ): MutableList<Pair<PsiElement, PsiElement>> {

        val stack = Stack<PsiElement>()
        val pairs = mutableListOf<Pair<PsiElement, PsiElement>>()
        val unmatchedClosings = mutableListOf<PsiElement>()
        val unmatchedOpenings = mutableListOf<PsiElement>()

        currentFile.accept(object : PsiRecursiveElementWalkingVisitor() {
            override fun visitElement(element: PsiElement) {
                //跳过注释||是否处于注释中
                if (element is PsiComment || element.parent is PsiComment) {
                    return
                }
                //字面量||是否处于字面量中
                if (element is PsiLiteralValue || element.parent is PsiLiteralValue || element.parent.parent is PsiLiteralValue) {
                    return
                }
                //println(element.text + "----------" + element.node.psi::class.simpleName)
                if (needRet(element)) return

                super.visitElement(element)
                if (element is ParenthesizedExpression){
                    val parenthesizedExpression = element
                    val expression = parenthesizedExpression.expression // 表达式内容
                    //val lParenth = parenthesizedExpression.lastChild // 左括号 PSI 元素
                    //val rParenth = parenthesizedExpression.rParenth // 右括号 PSI 元素

                }
                if (element.text == leftElement){
                    var r = element.parent.lastChild
                    var rElement = element.parent.lastChild
                    while (r.text != rightElement && rElement.prevSibling != null){
                        rElement = rElement.prevSibling
                        r = rElement.prevSibling
                    }

                    if (r != null){
                        pairs.add(Pair(element, r))
                    }
                }
            }
        })
        if (stack.isNotEmpty()) {
            //println("Unmatched opening brackets:")
            stack.forEach {
                unmatchedOpenings.add(it)
                //println("Opening bracket at ${it.textRange}")
            }
        }
        // Highlight unmatched opening brackets
        unmatchedOpenings.forEach {
            highlightBracket(
                markupModel,
                it,
                JBColor(Color.RED, Color.RED),
                document
            )
        }

        // Highlight unmatched closing brackets
        unmatchedClosings.forEach {
            highlightBracket(
                markupModel,
                it,
                JBColor(Color.RED, Color.RED),
                document
            )
        }
        return pairs
    }

    private fun highlightBrackets(pairs: MutableList<Pair<PsiElement, PsiElement>>) {

        pairs.forEach { (open, close) ->
            val color = getRandomColor()
            highlightBracket(markupModel, open, color, document)
            highlightBracket(markupModel, close, color, document)
            //println("Found bracket pair: (${open.textRange}, ${close.textRange})")
        }
    }

    private fun highlightBracket(
        markupModel: MarkupModel,
        element: PsiElement,
        color: Color,
        document: Document
    ) {
        val textAttributes = TextAttributes()

        textAttributes.foregroundColor = color
        if (element.startOffset >= 0 && element.endOffset <= document.textLength) {
            val highlighter = markupModel.addRangeHighlighter(
                element.textRange.startOffset,
                element.textRange.endOffset,
                HighlighterLayer.ADDITIONAL_SYNTAX,
                textAttributes,
                HighlighterTargetArea.EXACT_RANGE
            )
            highlighters.add(highlighter)
        }
    }

    private fun getRandomColor(): JBColor {
        val red = Random.nextInt(50, 256)
        val green = Random.nextInt(50, 256)
        val blue = Random.nextInt(50, 256)
        return JBColor(Color(red, green, blue), Color(red, green, blue))
    }

    private fun needRet(element: PsiElement): Boolean {
        return (element::class.simpleName == "KtStringTemplateExpression"
                || element::class.simpleName == "KtLiteralStringTemplateEntry"
                || element::class.simpleName == "KtBlockStringTemplateEntry"
                || ((element is LeafPsiElement) && element.elementType::class.simpleName == "KtToken")
                )
    }

    private var isNeedClean: Boolean = false
    private val highlighters = mutableListOf<RangeHighlighter>()
    private lateinit var markupModel: MarkupModel
    private lateinit var document: Document
    private lateinit var currentFile: PsiFile
    private lateinit var currentEditor: Editor
}

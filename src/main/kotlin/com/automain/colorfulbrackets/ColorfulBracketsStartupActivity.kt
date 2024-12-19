package com.automain.colorfulbrackets

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.lang.Language
import com.intellij.lang.LanguageAnnotators
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import java.util.concurrent.atomic.AtomicBoolean

class ColorfulBracketsStartupActivity : ProjectActivity, DumbAware {
    override suspend fun execute(project: Project) {
        ColorfulBracketsAnnotatorRegistrar.registerAnnotatorForAllLanguages()
        DaemonCodeAnalyzer.getInstance(project).restart()
    }
}

private object ColorfulBracketsAnnotatorRegistrar {
    private val registered = AtomicBoolean(false)

    fun registerAnnotatorForAllLanguages() {
        if (!registered.compareAndSet(false, true)) return

        val annotator = ColorfulBracketsAnnotator()
        Language.getRegisteredLanguages().forEach { language ->
            LanguageAnnotators.INSTANCE.addExplicitExtension(language, annotator)
        }
    }
}
package com.automain.colorfulbrackets.config

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class ProjectSettingsConfigurable : Configurable {
    private var projectSettingsConfigurableUI : ProjectSettingsConfigurableUI? = null

    override fun createComponent(): JComponent? {
        projectSettingsConfigurableUI = ProjectSettingsConfigurableUI()
        return projectSettingsConfigurableUI?.panel
    }

    override fun isModified(): Boolean {
        return projectSettingsConfigurableUI!!.isModifiedValue
    }

    override fun apply() {

    }

    override fun getDisplayName(): String {
        return "ColorfulBracketsSetting"
    }
}
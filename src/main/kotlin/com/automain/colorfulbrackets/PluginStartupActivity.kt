﻿package com.automain.colorfulbrackets

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

//项目启动时执行
class PluginStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        //文档修改之后触发
        val pluginDocumentListener = project.service<PluginDocumentListener>()
        pluginDocumentListener.register()
        //文件打开触发
        val pluginFileEditorManagerListener = project.service<PluginFileEditorManagerListener>()
        pluginFileEditorManagerListener.register()
        //Psi发生变化
        val pluginPsiTreeChangeListener = project.service<PluginPsiTreeChangeListener>()
        pluginPsiTreeChangeListener.register()
    }
}

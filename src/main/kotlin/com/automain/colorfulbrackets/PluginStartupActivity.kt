package com.automain.colorfulbrackets

//import com.automain.colorfulbrackets.listener.PluginDocumentListener
//import com.automain.colorfulbrackets.listener.PluginFileEditorManagerListener
//import com.automain.colorfulbrackets.listener.PluginPsiTreeChangeListener
import com.automain.colorfulbrackets.listener.PluginDocumentListener
import com.automain.colorfulbrackets.listener.PluginFileEditorManagerListener
import com.automain.colorfulbrackets.listener.PluginPsiTreeChangeListener
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.psi.PsiDocumentManager

//项目启动时执行
class PluginStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        BracketHelper.initBracketHelper()
        //插件初始化逻辑，仅在安装后执行一次
        if(!PropertiesComponent.getInstance().getBoolean("colorfulbracketsIsInit")){
            PropertiesComponent.getInstance().setValue("colorfulbracketsIsInit", true)
            PluginSetting.setBracketSetting("{}",true)
            PluginSetting.setBracketSetting("[]",true)
            PluginSetting.setBracketSetting("<>",true)
            PluginSetting.setBracketSetting("()",true)
        }
        //文档修改之后触发
        val pluginDocumentListener = project.service<PluginDocumentListener>()
        pluginDocumentListener.register()
        //文件打开触发
        val pluginFileEditorManagerListener = project.service<PluginFileEditorManagerListener>()
        pluginFileEditorManagerListener.register()
        //Psi发生变化
        val pluginPsiTreeChangeListener = project.service<PluginPsiTreeChangeListener>()
        pluginPsiTreeChangeListener.register()
        //启动后直接执行一次
        ApplicationManager.getApplication().invokeLater {
            val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return@invokeLater
            val document = editor.document
            val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return@invokeLater
            BracketFinder.findBrackets(psiFile, editor)
        }
    }
}

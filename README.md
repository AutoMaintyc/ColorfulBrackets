# ColorfulBrackets
In programming. Literal meaning - ColorfulBrackets.
## 功能：
让代码中 成对 出现的 “{”和“}” 变为随机的颜色，增加代码可读性
## 后续计划：
可配置的颜色
## 注：
目前仅支持，Jetbrains的2023.1及以上版本

## 实现：
###  一、入口：
#### PSI：
PluginPsiTreeChangeListener
<br>继承自 PsiTreeChangeListener
#### 主动刷新：
FindBracketsAction
<br>在plugin.xml中绑定
#### 文档变化：
PluginFileEditorManagerListener
<br>继承自 FileEditorManagerListener
### 二、配置界面：
#### 界面事件：
ProjectSettingsConfigurable
<br>继承自 Configurable
#### 界面：
ProjectSettingsConfigurableUI

﻿package com.automain.colorfulbrackets.config

import com.automain.colorfulbrackets.BracketFinder
import com.automain.colorfulbrackets.PluginSetting
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.observable.util.addComponent
import com.intellij.ui.components.*
import java.awt.Component
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class ProjectSettingsConfigurableUI {
    val panel: JPanel = JPanel()
    val listModel: DefaultListModel<Pair<String, Boolean>> = DefaultListModel()
    private val selectList: JList<Pair<String, Boolean>> = JBList(listModel)
    var isModifiedValue : Boolean = false

    init {
        // 添加一些选项到列表中
        listModel.addElement(Pair("{}",PropertiesComponent.getInstance().getBoolean("{}")))
        listModel.addElement(Pair("[]",PropertiesComponent.getInstance().getBoolean("[]")))
        listModel.addElement(Pair("<>",PropertiesComponent.getInstance().getBoolean("<>")))
        listModel.addElement(Pair("()",PropertiesComponent.getInstance().getBoolean("()")))

        // 设置列表的渲染器为 JCheckBox
        selectList.cellRenderer = object : DefaultListCellRenderer() {
            override fun getListCellRendererComponent(list: JList<*>?, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
                val item = value as Pair<*, *>
                val checkbox = JCheckBox(item.first as String)
                checkbox.isSelected = item.second as Boolean
                return checkbox
            }
        }

        // 添加一个鼠标点击事件监听器
        selectList.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(event: MouseEvent) {
                val index = selectList.locationToIndex(event.point)
                val item = selectList.model.getElementAt(index) as Pair<String, Boolean>
                // 更新选中状态
                listModel.setElementAt(Pair(item.first, !item.second), index)
                PluginSetting.setBracketSetting(item.first, !item.second)
                selectList.repaint()
                isModifiedValue = true
                BracketFinder.find()
            }
        })

        // 设置列表的选择模式
        selectList.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION

        // 将列表和详情面板添加到分割面板中
        panel.addComponent(selectList)
    }
}

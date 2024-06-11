package com.automain.colorfulbrackets.config

import com.intellij.ui.components.*
import javax.swing.*

class ProjectSettingsConfigurableUI {
    val panel: JSplitPane = JSplitPane()
    val listModel: DefaultListModel<String> = DefaultListModel()
    val myList: JList<String> = JBList(listModel)
    val detailPanel: JPanel = JPanel()

    init {
        // 添加一些选项到列表中
        listModel.addElement("Option 1")
        listModel.addElement("Option 2")
        listModel.addElement("Option 3")

        // 设置列表的选择模式为单选
        myList.selectionMode = ListSelectionModel.SINGLE_SELECTION

        // 添加一个列表选择事件监听器
        myList.addListSelectionListener {
            // 当用户选择一个选项时，更新详情面板
            val selectedOption = myList.selectedValue
            detailPanel.removeAll()
            detailPanel.add(JLabel("You selected: $selectedOption"))
            detailPanel.revalidate()
            detailPanel.repaint()
        }

        // 将列表和详情面板添加到分割面板中
        panel.leftComponent = JScrollPane(myList)
        panel.rightComponent = detailPanel
    }
}

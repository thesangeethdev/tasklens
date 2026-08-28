package com.sangeeth.tasklens.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.sangeeth.tasklens.service.NavigationService
import java.awt.BorderLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel

class TodoToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val tableModel = TodoTableModel()
        val table = com.intellij.ui.table.JBTable(tableModel)
        val filterPanel = TodoFilterPanel(tableModel)

        table.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 2 && table.selectedRow >= 0) {
                    val item = tableModel.getItemAt(table.selectedRow)
                    NavigationService.open(project, item)
                }
            }
        })

        val panel = JPanel(BorderLayout())
        panel.add(filterPanel, BorderLayout.NORTH)
        panel.add(com.intellij.ui.components.JBScrollPane(table), BorderLayout.CENTER)

        val content = ContentFactory.getInstance().createContent(panel, "", false)
        toolWindow.contentManager.addContent(content)

        currentModel = tableModel
    }

    companion object {
        private var currentModel: TodoTableModel? = null

        fun refreshAll() {
            currentModel?.refresh()
        }
    }
}
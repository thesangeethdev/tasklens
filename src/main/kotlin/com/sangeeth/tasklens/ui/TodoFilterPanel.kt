package com.sangeeth.tasklens.ui

import com.sangeeth.tasklens.model.Priority
import com.sangeeth.tasklens.model.TodoType
import java.awt.FlowLayout
import javax.swing.*

class TodoFilterPanel(private val tableModel: TodoTableModel) : JPanel(FlowLayout(FlowLayout.LEFT)) {
    private val searchField = JTextField(20)
    private val todoCheck = JCheckBox("TODO", true)
    private val fixmeCheck = JCheckBox("FIXME", true)
    private val hackCheck = JCheckBox("HACK", true)
    private val highCheck = JCheckBox("HIGH", true)
    private val medCheck = JCheckBox("MED", true)
    private val lowCheck = JCheckBox("LOW", true)

    init {
        add(JLabel("Search:"))
        add(searchField)

        add(JLabel("Types:"))
        add(todoCheck)
        add(fixmeCheck)
        add(hackCheck)

        add(JLabel("Priority:"))
        add(highCheck)
        add(medCheck)
        add(lowCheck)

        val applyButton = JButton("Apply Filter")
        applyButton.addActionListener { applyFilter() }
        add(applyButton)

        val refreshButton = JButton("Refresh")
        refreshButton.addActionListener { tableModel.refresh() }
        add(refreshButton)
    }

    private fun applyFilter() {
        val types = mutableSetOf<TodoType>()
        if (todoCheck.isSelected) types.add(TodoType.TODO)
        if (fixmeCheck.isSelected) types.add(TodoType.FIXME)
        if (hackCheck.isSelected) types.add(TodoType.HACK)

        val priorities = mutableSetOf<Priority>()
        if (highCheck.isSelected) priorities.add(Priority.HIGH)
        if (medCheck.isSelected) priorities.add(Priority.MEDIUM)
        if (lowCheck.isSelected) priorities.add(Priority.LOW)

        tableModel.applyFilter(searchField.text, types, priorities)
    }
}
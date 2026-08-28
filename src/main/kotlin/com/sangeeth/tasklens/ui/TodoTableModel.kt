package com.sangeeth.tasklens.ui

import com.sangeeth.tasklens.model.Priority
import com.sangeeth.tasklens.model.TodoItem
import com.sangeeth.tasklens.model.TodoType
import com.sangeeth.tasklens.service.TodoRepository
import javax.swing.table.AbstractTableModel

class TodoTableModel : AbstractTableModel() {
    private var filteredItems: List<TodoItem> = emptyList()
    private val columns = arrayOf("Type", "Priority", "Message", "File", "Line")

    init {
        refresh()
    }

    fun refresh() {
        filteredItems = TodoRepository.getAll()
        fireTableDataChanged()
    }

    fun applyFilter(searchText: String, types: Set<TodoType>, priorities: Set<Priority>) {
        filteredItems = TodoRepository.getAll().filter { item ->
            val matchesText = searchText.isBlank() ||
                    item.message.contains(searchText, ignoreCase = true) ||
                    item.fileName.contains(searchText, ignoreCase = true)
            val matchesType = types.isEmpty() || item.type in types
            val matchesPriority = priorities.isEmpty() || item.priority in priorities
            matchesText && matchesType && matchesPriority
        }
        fireTableDataChanged()
    }

    fun getItemAt(row: Int): TodoItem = filteredItems[row]

    override fun getRowCount(): Int = filteredItems.size
    override fun getColumnCount(): Int = columns.size
    override fun getColumnName(column: Int): String = columns[column]

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        val item = filteredItems[rowIndex]
        return when (columnIndex) {
            0 -> item.type.label
            1 -> item.priority.label
            2 -> item.message
            3 -> item.fileName
            4 -> item.lineNumber
            else -> ""
        }
    }
}
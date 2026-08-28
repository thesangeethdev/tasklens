package com.sangeeth.tasklens.service

import com.sangeeth.tasklens.model.TodoItem
import java.util.concurrent.ConcurrentHashMap

object TodoRepository {
    private val todos = ConcurrentHashMap<String, MutableList<TodoItem>>()

    fun add(item: TodoItem) {
        todos.getOrPut(item.filePath) { mutableListOf() }.add(item)
    }

    fun getAll(): List<TodoItem> = todos.values.flatten().sortedBy { it.fileName }

    fun clear() = todos.clear()
}
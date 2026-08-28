package com.sangeeth.tasklens.model

data class TodoItem(
    val type: TodoType,
    val message: String,
    val filePath: String,
    val fileName: String,
    val lineNumber: Int,
    val priority: Priority = Priority.LOW
)
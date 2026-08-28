package com.sangeeth.tasklens.service

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.sangeeth.tasklens.model.TodoItem

object NavigationService {
    fun open(project: Project, todo: TodoItem) {
        val virtualFile = LocalFileSystem.getInstance().findFileByPath(todo.filePath) ?: return
        val descriptor = OpenFileDescriptor(project, virtualFile, todo.lineNumber - 1, 0)
        FileEditorManager.getInstance(project).openTextEditor(descriptor, true)
    }
}
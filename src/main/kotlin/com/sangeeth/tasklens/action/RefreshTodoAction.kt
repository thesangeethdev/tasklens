package com.sangeeth.tasklens.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.sangeeth.tasklens.service.TodoIndexService

class RefreshTodoAction : AnAction("Refresh TaskLens") {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        TodoIndexService(project).scanProject()
    }
}
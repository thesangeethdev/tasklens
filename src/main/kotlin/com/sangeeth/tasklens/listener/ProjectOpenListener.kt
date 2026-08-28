package com.sangeeth.tasklens.listener


import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.sangeeth.tasklens.service.TodoIndexService

class ProjectOpenListener : StartupActivity {
    override fun runActivity(project: Project) {
        TodoIndexService(project).scanProject()
    }
}
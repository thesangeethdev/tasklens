package com.sangeeth.tasklens.listener


import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.psi.PsiManager
import com.sangeeth.tasklens.service.TodoIndexService

class ProjectOpenListener : StartupActivity {
    override fun runActivity(project: Project) {
        // Initial full scan
        TodoIndexService(project).scanProject()

        // Listen for real-time changes
        PsiManager.getInstance(project).addPsiTreeChangeListener(
            TodoPsiTreeChangeListener(project),
            project  // This automatically unregisters when project closes
        )
    }
}
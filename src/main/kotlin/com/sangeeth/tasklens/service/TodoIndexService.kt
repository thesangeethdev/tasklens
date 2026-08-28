package com.sangeeth.tasklens.service

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.openapi.application.ApplicationManager
import com.sangeeth.tasklens.model.Priority
import com.sangeeth.tasklens.model.TodoItem
import com.sangeeth.tasklens.model.TodoType
import com.sangeeth.tasklens.ui.TodoToolWindowFactory
import java.util.regex.Pattern

class TodoIndexService(private val project: Project) {

    fun scanProject() {
        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Scanning TODOs...") {
            override fun run(indicator: ProgressIndicator) {
                TodoRepository.clear()
                val fileIndex = ProjectRootManager.getInstance(project).fileIndex
                val extensions = setOf("kt", "java", "xml")

                fileIndex.iterateContent { file: VirtualFile ->
                    indicator.checkCanceled()
                    if (!file.isDirectory && file.extension?.lowercase() in extensions) {
                        scanFile(file)
                    }
                    true
                }

                ApplicationManager.getApplication().invokeLater {
                    TodoToolWindowFactory.refreshAll()
                }
            }
        })
    }

    private fun scanFile(virtualFile: VirtualFile) {
        val psiFile = PsiManager.getInstance(project).findFile(virtualFile) ?: return
        val comments = PsiTreeUtil.findChildrenOfType(psiFile, PsiComment::class.java)

        for (comment in comments) {
            val text = comment.text ?: continue
            val document = psiFile.viewProvider.document
            val lineNumber = document?.getLineNumber(comment.textOffset)?.plus(1) ?: 0
            val todo = extractTodo(text, virtualFile.path, virtualFile.name, lineNumber) ?: continue
            TodoRepository.add(todo)
        }
    }

    private fun extractTodo(text: String, filePath: String, fileName: String, lineNumber: Int): TodoItem? {
        val pattern = Pattern.compile(
            "(TODO|FIXME|HACK)[\\s:]*\\[?(HIGH|MEDIUM|LOW)?\\]?[:\\s-]*(.*)",
            Pattern.CASE_INSENSITIVE
        )
        val matcher = pattern.matcher(text)

        if (matcher.find()) {
            val typeStr = matcher.group(1)?.uppercase() ?: return null
            val priorityStr = matcher.group(2)?.uppercase() ?: "LOW"
            val message = matcher.group(3)?.trim() ?: ""

            val type = when (typeStr) {
                "FIXME" -> TodoType.FIXME
                "HACK" -> TodoType.HACK
                else -> TodoType.TODO
            }

            val priority = when (priorityStr) {
                "HIGH" -> Priority.HIGH
                "MEDIUM" -> Priority.MEDIUM
                else -> Priority.LOW
            }

            return TodoItem(type, message, filePath, fileName, lineNumber, priority)
        }
        return null
    }
}
package com.sangeeth.tasklens.listener

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.sangeeth.tasklens.model.Priority
import com.sangeeth.tasklens.model.TodoItem
import com.sangeeth.tasklens.model.TodoType
import com.sangeeth.tasklens.service.TodoRepository
import com.sangeeth.tasklens.ui.TodoToolWindowFactory
import java.util.regex.Pattern

class TodoPsiTreeChangeListener(private val project: Project) : PsiTreeChangeListener {

    private val todoPattern = Pattern.compile(
        "(TODO|FIXME|HACK)[\\s:]*\\[?(HIGH|MEDIUM|LOW)?\\]?[:\\s-]*(.*)",
        Pattern.CASE_INSENSITIVE
    )

    override fun beforeChildAddition(event: PsiTreeChangeEvent) {}
    override fun beforeChildRemoval(event: PsiTreeChangeEvent) {}
    override fun beforeChildReplacement(event: PsiTreeChangeEvent) {}
    override fun beforeChildMovement(event: PsiTreeChangeEvent) {}
    override fun beforeChildrenChange(event: PsiTreeChangeEvent) {}
    override fun beforePropertyChange(event: PsiTreeChangeEvent) {}

    override fun childAdded(event: PsiTreeChangeEvent) { handleChange(event) }
    override fun childRemoved(event: PsiTreeChangeEvent) { handleChange(event) }
    override fun childReplaced(event: PsiTreeChangeEvent) { handleChange(event) }
    override fun childrenChanged(event: PsiTreeChangeEvent) { handleChange(event) }
    override fun childMoved(event: PsiTreeChangeEvent) { handleChange(event) }
    override fun propertyChanged(event: PsiTreeChangeEvent) { handleChange(event) }

    private fun handleChange(event: PsiTreeChangeEvent) {
        val psiFile = event.file ?: return
        val ext = psiFile.name.substringAfterLast('.', "").lowercase()
        if (ext !in setOf("kt", "java", "xml")) return

        val virtualFile = psiFile.virtualFile ?: return
        val filePath = virtualFile.path

        // Check if a comment was involved
        val changed = event.child ?: event.parent ?: return
        val hasComment = changed is PsiComment
                || event.oldChild is PsiComment
                || event.newChild is PsiComment
                || PsiTreeUtil.findChildrenOfType(changed, PsiComment::class.java).isNotEmpty()

        if (!hasComment) return

        // Re-scan just this file
        TodoRepository.clearFile(filePath)

        val comments = PsiTreeUtil.findChildrenOfType(psiFile, PsiComment::class.java)
        for (comment in comments) {
            val text = comment.text ?: continue
            val document = psiFile.viewProvider.document
            val lineNumber = document?.getLineNumber(comment.textOffset)?.plus(1) ?: 0
            extractTodo(text, filePath, psiFile.name, lineNumber)?.let {
                TodoRepository.add(it)
            }
        }

        ApplicationManager.getApplication().invokeLater {
            TodoToolWindowFactory.refreshAll()
        }
    }

    private fun extractTodo(text: String, filePath: String, fileName: String, lineNumber: Int): TodoItem? {
        val matcher = todoPattern.matcher(text)
        if (!matcher.find()) return null

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
}
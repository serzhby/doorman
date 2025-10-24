package com.github.serzhby.tools.plugins.boundary.actions

import com.github.serzhby.tools.plugins.boundary.services.BoundaryTargetsLoader
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.ActionUiKind
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.ex.ActionUtil
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

class RefreshItemsAction : AnAction(
  "Reload Items",
  "Fetch items from the backend and refresh the tree",
  AllIcons.Actions.Refresh
) {
  override fun actionPerformed(e: AnActionEvent) {
    val project = e.project ?: return
    project.service<BoundaryTargetsLoader>().loadTree()
  }

  companion object {
    fun trigger(project: Project) {
      val refreshAction = ActionManager.getInstance().getAction("Boundary.RefreshItemsAction")
        ?: throw IllegalStateException("RefreshItemsAction not found in ActionManager.")
      ApplicationManager.getApplication().invokeLater {
        val dataContext = SimpleDataContext.builder()
          .add(CommonDataKeys.PROJECT, project)
          .build()
        ActionUtil.invokeAction(
          refreshAction,
          AnActionEvent.createEvent(dataContext, null, ActionPlaces.TOOLWINDOW_TOOLBAR_BAR, ActionUiKind.Companion.TOOLBAR, null),
          null
        )
      }
    }
  }
}
package com.github.serzhby.tools.plugins.boundary.actions

import com.github.serzhby.tools.plugins.boundary.services.BoundarySettings
import com.github.serzhby.tools.plugins.boundary.services.Host
import com.github.serzhby.tools.plugins.boundary.ui.toolwindow.BoundaryTree
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKey

class DeleteHostAction : AnAction("Delete Host") {

  override fun update(e: AnActionEvent) {
    e.presentation.isEnabledAndVisible = e.getData(BoundaryTree.selectedNodeDataKey) == "host"
  }

  override fun actionPerformed(e: AnActionEvent) {
    val project = e.project ?: return
    val host = e.getData<Host>(DataKey.create("host"))
      ?: throw IllegalStateException("BoundaryTarget data not found in action event.")

    BoundarySettings.getInstance().state.apply {
      hosts = hosts.filter { it.url != host.url }.toMutableList()
    }

    RefreshItemsAction.trigger(project)
  }
}
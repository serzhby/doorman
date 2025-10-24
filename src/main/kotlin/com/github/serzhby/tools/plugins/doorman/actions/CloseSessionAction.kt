package com.github.serzhby.tools.plugins.doorman.actions

import com.github.serzhby.tools.plugins.doorman.BoundaryBundle
import com.github.serzhby.tools.plugins.doorman.model.CloseSessionResult
import com.github.serzhby.tools.plugins.doorman.model.Session
import com.github.serzhby.tools.plugins.doorman.services.BoundaryService
import com.github.serzhby.tools.plugins.doorman.services.Host
import com.github.serzhby.tools.plugins.doorman.ui.toolwindow.BoundaryTree
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKey
import com.intellij.openapi.application.EDT
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.platform.ide.progress.withBackgroundProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CloseSessionAction(
  private val cs: CoroutineScope
) : AnAction("Close Session") {
  override fun update(e: AnActionEvent) {
    e.presentation.isEnabledAndVisible = e.getData(BoundaryTree.selectedNodeDataKey) == "session"
  }

  override fun actionPerformed(e: AnActionEvent) {
    val project = e.project ?: return
    val host = e.getData<Host>(DataKey.create("host"))
      ?: throw IllegalStateException("Host data not found in action event.")

    cs.launch {
      val session = e.getData<Session>(DataKey.create("session"))
        ?: throw IllegalStateException("BoundaryTarget data not found in action event.")
      withBackgroundProgress(project, BoundaryBundle.message("closeSessionAction.progress.title", session.id)) {
        val result = withContext(Dispatchers.IO) {
          project.service<BoundaryService>().closeSession(host, session)
        }
        withContext(Dispatchers.EDT) {
          if (result.isOk()) {
            RefreshItemsAction.trigger(project)
          } else {
            showNotification(result, project)
          }
        }
      }
    }
  }

  private fun showNotification(
    result: CloseSessionResult,
    project: Project
  ) {
    Notifications.Bus.notify(
      Notification(
        "Boundary",
        BoundaryBundle.message("closeSessionAction.error.title"),
        result.context ?: result.toString(),
        NotificationType.ERROR
      ),
      project
    )
  }
}
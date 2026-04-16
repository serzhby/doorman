package com.github.serzhby.tools.plugins.doorman.actions

import com.github.serzhby.tools.plugins.doorman.BoundaryBundle
import com.github.serzhby.tools.plugins.doorman.boundary.AuthMethod
import com.github.serzhby.tools.plugins.doorman.boundary.exceptions.UnsupportedAuthMethodException
import com.github.serzhby.tools.plugins.doorman.services.BoundaryService
import com.github.serzhby.tools.plugins.doorman.services.Host
import com.github.serzhby.tools.plugins.doorman.ui.toolwindow.BoundaryTree
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKey
import com.intellij.openapi.application.EDT
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.platform.ide.progress.withModalProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthorizeAction(
  private val cs: CoroutineScope
) : AnAction() {

  override fun update(e: AnActionEvent) {
    e.presentation.isEnabledAndVisible = e.getData(BoundaryTree.selectedNodeDataKey) == "authMethod"
  }

  override fun actionPerformed(e: AnActionEvent) {
    val project = e.project ?: return
    val authMethod = e.getData(AUTH_METHOD_DATA_KEY) ?: return
    val host = e.getData(HOST_DATA_KEY) ?: return
    cs.launch {
      try {
        withModalProgress(project, BoundaryBundle.message("authDialog.title", authMethod.type, authMethod.name)) {
          withContext(Dispatchers.IO) {
            project.service<BoundaryService>().authenticate(host, authMethod)
          }
        }
      } catch(e: UnsupportedAuthMethodException) {
        showUnsupportedAuthMethodDialog(e.authMethod)
      }
      refreshItemsList(project)
    }
  }

  private suspend fun showUnsupportedAuthMethodDialog(authMethod: AuthMethod) {
    withContext(Dispatchers.EDT) {
      Messages.showErrorDialog(
        BoundaryBundle.message("unsupportedAuthMethodDialog.text", authMethod.type),
        BoundaryBundle.message("unsupportedAuthMethodDialog.title")
      )
    }
  }

  private suspend fun refreshItemsList(project: Project) {
    withContext(Dispatchers.EDT) {
      RefreshItemsAction.trigger(project)
    }
  }

  companion object {
    val AUTH_METHOD_DATA_KEY = DataKey.create<AuthMethod>("authMethod")
    val HOST_DATA_KEY = DataKey.create<Host>("host")
  }
}
package com.github.serzhby.tools.plugins.boundary.actions

import com.github.serzhby.tools.plugins.boundary.model.AuthMethod
import com.github.serzhby.tools.plugins.boundary.services.BoundaryService
import com.github.serzhby.tools.plugins.boundary.services.Host
import com.github.serzhby.tools.plugins.boundary.ui.toolwindow.BoundaryTree
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKey
import com.intellij.openapi.application.EDT
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
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
    val authResponse = cs.launch {
      withContext(Dispatchers.IO) {
        project.service<BoundaryService>().authenticate(host, authMethod)
      }
      withContext(Dispatchers.EDT) {
        RefreshItemsAction.trigger(project)
      }
    }
    thisLogger().info("Authorization response: $authResponse")
  }

  companion object {
    val AUTH_METHOD_DATA_KEY = DataKey.create<AuthMethod>("authMethod")
    val HOST_DATA_KEY = DataKey.create<Host>("host")
  }
}
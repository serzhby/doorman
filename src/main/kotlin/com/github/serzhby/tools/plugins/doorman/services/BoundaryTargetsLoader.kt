package com.github.serzhby.tools.plugins.doorman.services

import com.github.serzhby.tools.plugins.doorman.BoundaryBundle
import com.github.serzhby.tools.plugins.doorman.model.HostModel
import com.github.serzhby.tools.plugins.doorman.ui.toolwindow.BoundaryToolWindowFactory
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.EDT
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.platform.ide.progress.withBackgroundProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Service(Service.Level.PROJECT)
class BoundaryTargetsLoader(
  private val project: Project,
  private val cs: CoroutineScope,
) {

  private lateinit var toolWindow: BoundaryToolWindowFactory.BoundaryToolWindow

  private val boundaryService: BoundaryService =
    ApplicationManager.getApplication().getService(BoundaryService::class.java)

  fun bind(toolWindow: BoundaryToolWindowFactory.BoundaryToolWindow) {
    this.toolWindow = toolWindow
  }

  fun loadTree() = cs.launch {
    withBackgroundProgress(project, BoundaryBundle.message("loadingResources")) {
      val hosts = BoundarySettings.getInstance().state.hosts
      withContext(Dispatchers.EDT) {
        toolWindow.initTree(hosts.map { HostModel(it) })
      }
      val hostModels = withContext(Dispatchers.IO) {
        hosts.map { host ->
          loadForHost(host)
        }
      }

      withContext(Dispatchers.EDT) {
        toolWindow.updateTree(hostModels)
      }
    }
  }

  fun loadForHost(host: Host): HostModel {
    val resources = boundaryService.listResources(host)
    return if (resources.isOk()) {
      val sessions = boundaryService.listSessions(host)
      HostModel(host, resources.items, sessions.items)
    } else {
      val authMethods = boundaryService.listAuthMethods(host)
      HostModel(
        host = host,
        authMethods = authMethods.items
      )
    }
  }

}

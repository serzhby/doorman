package com.github.serzhby.tools.plugins.boundary.ui.toolwindow

import com.github.serzhby.tools.plugins.boundary.BoundaryBundle
import com.github.serzhby.tools.plugins.boundary.model.AuthMethod
import com.github.serzhby.tools.plugins.boundary.model.BoundaryTarget
import com.github.serzhby.tools.plugins.boundary.model.HostModel
import com.github.serzhby.tools.plugins.boundary.model.Session
import com.github.serzhby.tools.plugins.boundary.actions.RefreshItemsAction
import com.github.serzhby.tools.plugins.boundary.services.BoundaryTargetsLoader
import com.github.serzhby.tools.plugins.boundary.services.Host
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.ActionUiKind
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataKey
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.ex.ActionUtil
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.PopupHandler
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.TreeUIHelper
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.ui.tree.TreeUtil
import java.awt.event.ActionEvent
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

class BoundaryToolWindowFactory : ToolWindowFactory {

  override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
    val boundaryToolWindow = BoundaryToolWindow(toolWindow)
    val content = ContentFactory.getInstance().createContent(boundaryToolWindow.content, null, false)
    toolWindow.contentManager.addContent(content)
    project.service<BoundaryTargetsLoader>().bind(boundaryToolWindow)
  }

  override fun shouldBeAvailable(project: Project) = true

  class BoundaryToolWindow(private val toolWindow: ToolWindow) {

    private val project: Project = toolWindow.project
    private lateinit var tree: Tree


    private val addHostAction = ActionManager.getInstance().getAction("Boundary.AddHostAction")
    private val refreshAction = ActionManager.getInstance().getAction("Boundary.RefreshItemsAction")
    private val createDataSourceAction = ActionManager.getInstance().getAction("Boundary.CreateDataSourceAction")

    val content: SimpleToolWindowPanel by lazy {
      SimpleToolWindowPanel(true, true).apply {

        val actionGroup = DefaultActionGroup().apply {
          add(addHostAction)
          add(refreshAction)
        }

        val toolbar = ActionManager.getInstance()
          .createActionToolbar(
            ActionPlaces.TOOLWINDOW_TOOLBAR_BAR,
            actionGroup,
            true
          )
          .apply { targetComponent = content }
        setToolbar(toolbar.component)

        val rootNode = DefaultMutableTreeNode()
        val defaultTreeModel = DefaultTreeModel(rootNode)
        tree = BoundaryTree(defaultTreeModel).apply {
          emptyText.appendText(
            BoundaryBundle.message("boundaryToolWindow.emptyText"),
            SimpleTextAttributes.REGULAR_ATTRIBUTES,
            this@BoundaryToolWindow::triggerAddHostAction
          )
          emptyText.appendSecondaryText(
            BoundaryBundle.message("boundaryToolWindow.emptySecondaryText"),
            SimpleTextAttributes.GRAYED_ATTRIBUTES,
            this@BoundaryToolWindow::triggerAddHostAction
          )
          isRootVisible = false
          cellRenderer = TreeItemsRenderer()
          TreeUIHelper.getInstance().installTreeSpeedSearch(this);
          PopupHandler.installPopupMenu(this, "BoundaryTree.Popup", ActionPlaces.TOOLWINDOW_POPUP)
        }
        val jbScrollPane = JBScrollPane(tree)
        TreeDoubleClickListener(this@BoundaryToolWindow::doAction).installOn(tree)
        TreeEnterKeyListener(this@BoundaryToolWindow::doAction).installOn(tree)
        setContent(jbScrollPane)

        RefreshItemsAction.trigger(project)
      }
    }

    fun initTree(hostModels: List<HostModel>) {
      if (tree.model.root == null) {
        updateTree(hostModels)
      }
    }

    fun updateTree(hostModels: List<HostModel>) {
      val root = DefaultMutableTreeNode()
      hostModels.forEach { hostModel ->
        val hostNode = DefaultMutableTreeNode(hostModel.host)
        root.add(hostNode)
        if (hostModel.authMethods.isEmpty()) {
          fillTree(hostNode, hostModel.targets, hostModel.sessions)
        } else {
          fillTree(hostNode, hostModel.authMethods)
        }
      }
      (tree.model as DefaultTreeModel).setRoot(root)
      TreeUtil.expand(tree, 3)
    }

    private fun fillTree(
      root: DefaultMutableTreeNode,
      items: List<BoundaryTarget>,
      sessions: List<Session>
    ) {
      val itemsByScope = items.groupBy { it.scope }
      itemsByScope.entries.forEach { (scope, items) ->
        val scopeNode = DefaultMutableTreeNode(scope)
        root.add(scopeNode)
        fillScopeItems(scopeNode, items, sessions)
      }
    }

    private fun fillTree(
      root: DefaultMutableTreeNode,
      authMethods: List<AuthMethod>
    ) {
      authMethods.forEach { authMethod ->
        val node = DefaultMutableTreeNode(authMethod)
        root.add(node)
      }
    }

    private fun fillScopeItems(
      scopeNode: DefaultMutableTreeNode,
      items: List<BoundaryTarget>,
      sessions: List<Session>
    ) {
      items.forEach { item ->
        val itemNode = DefaultMutableTreeNode(item)
//        fillResourceNode(itemNode, item, sessions)
        scopeNode.add(itemNode)
      }
    }

    private fun fillResourceNode(
      resourceNode: DefaultMutableTreeNode,
      item: BoundaryTarget,
      sessions: List<Session>
    ) {
      sessions
        .filter { it.targetId == item.id }
        .forEach { session ->
          val sessionNode = DefaultMutableTreeNode(session)
          resourceNode.add(sessionNode)
        }
    }

    private fun triggerAddHostAction(actionEvent: ActionEvent) {
      ApplicationManager.getApplication().invokeLater {
        val dataContext = SimpleDataContext.builder()
          .add(CommonDataKeys.PROJECT, project)
          .build()
        ActionUtil.invokeAction(
          addHostAction,
          AnActionEvent.createEvent(dataContext, null, ActionPlaces.TOOLWINDOW_TOOLBAR_BAR, ActionUiKind.TOOLBAR, null),
          null
        )
      }
    }

    private fun doAction(host: Host, target: BoundaryTarget) {
      ApplicationManager.getApplication().invokeLater {
        val dataContext = SimpleDataContext.builder()
          .add(CommonDataKeys.PROJECT, project)
          .add(DataKey.create("host"), host)
          .add(DataKey.create("target"), target)
          .build()
        ActionUtil.invokeAction(
          createDataSourceAction,
          AnActionEvent.createEvent(dataContext, null, ActionPlaces.TOOLWINDOW_TOOLBAR_BAR, ActionUiKind.TOOLBAR, null),
          null
        )
      }

      RefreshItemsAction.trigger(project)
    }
  }
}

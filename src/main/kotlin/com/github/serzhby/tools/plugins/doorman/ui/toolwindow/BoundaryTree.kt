package com.github.serzhby.tools.plugins.doorman.ui.toolwindow

import com.github.serzhby.tools.plugins.doorman.boundary.AuthMethod
import com.github.serzhby.tools.plugins.doorman.boundary.BoundaryTarget
import com.github.serzhby.tools.plugins.doorman.boundary.Scope
import com.github.serzhby.tools.plugins.doorman.boundary.Session
import com.github.serzhby.tools.plugins.doorman.services.Host
import com.intellij.openapi.actionSystem.DataKey
import com.intellij.openapi.actionSystem.DataSink
import com.intellij.openapi.actionSystem.UiDataProvider
import com.intellij.ui.treeStructure.Tree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeModel

class BoundaryTree(treeModel: TreeModel) : Tree(treeModel), UiDataProvider {

  override fun uiDataSnapshot(sink: DataSink) {
    val node = lastSelectedPathComponent as? DefaultMutableTreeNode ?: return
    val hostNode = node.userObjectPath
      .firstOrNull { it is Host }
    if (hostNode != null) {
      sink[DataKey.create("host")] = hostNode
    }
    val userObjectLabel = node.userObject?.let(this::getLabel)
    if (userObjectLabel != null) {
      sink[DataKey.create(userObjectLabel)] = node.userObject
      sink[selectedNodeDataKey] = userObjectLabel
    }
  }

  private fun getLabel(obj: Any): String? {
    return when (obj) {
      is Host -> "host"
      is BoundaryTarget -> "target"
      is Session -> "session"
      is Scope -> "scope"
      is AuthMethod -> "authMethod"
      else -> null
    }
  }

  companion object {
    val selectedNodeDataKey = DataKey.create<String>("selectedNode")
  }
}
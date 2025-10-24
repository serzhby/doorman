package com.github.serzhby.tools.plugins.doorman.ui.toolwindow

import com.github.serzhby.tools.plugins.doorman.model.BoundaryTarget
import com.github.serzhby.tools.plugins.doorman.services.Host
import com.intellij.openapi.ui.addKeyboardAction
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.ui.tree.TreeUtil
import javax.swing.KeyStroke
import javax.swing.tree.DefaultMutableTreeNode

class TreeEnterKeyListener(
  private val action: (Host, BoundaryTarget) -> Unit,
) {

  fun installOn(tree: Tree) {
    tree.addKeyboardAction(KeyStroke.getKeyStroke("ENTER")) {
      val selectedNodes = tree.getSelectedNodes(DefaultMutableTreeNode::class.java, null)
      if (selectedNodes.size == 1) {
        val node = selectedNodes.first()
        val target = node.userObject
        if (target is BoundaryTarget) {
          val hostNode = (node as DefaultMutableTreeNode).parent.parent
          val host = TreeUtil.getUserObject(hostNode) as? Host ?: throw IllegalStateException("Host node not found")
          action(host, target)
        }
      }
    }
  }
}
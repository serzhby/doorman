package com.github.serzhby.tools.plugins.boundary.ui.toolwindow

import com.github.serzhby.tools.plugins.boundary.model.BoundaryTarget
import com.github.serzhby.tools.plugins.boundary.services.Host
import com.intellij.ui.DoubleClickListener
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.EditSourceOnDoubleClickHandler
import com.intellij.util.ui.tree.TreeUtil
import java.awt.Component
import java.awt.event.MouseEvent
import javax.swing.tree.DefaultMutableTreeNode

class TreeDoubleClickListener(
  private val action: (Host, BoundaryTarget) -> Unit,
) : DoubleClickListener() {

  private lateinit var tree: Tree

  override fun installOn(c: Component) {
    if (c !is Tree) {
      throw IllegalArgumentException("Component must be a Tree")
    }
    this.tree = c
    super.installOn(c)
  }

  override fun onDoubleClick(e: MouseEvent): Boolean {
    if (EditSourceOnDoubleClickHandler.isToggleEvent(tree, e)) return false

    val path = tree.getPathForLocation(e.x, e.y) ?: return false
    val node = TreeUtil.getUserObject(path.lastPathComponent) ?: return false
    if (node !is BoundaryTarget) return true
    val hostNode = (path.lastPathComponent as DefaultMutableTreeNode).parent.parent
    val host = TreeUtil.getUserObject(hostNode) as? Host ?: throw IllegalStateException("Host node not found")
    action(host, node)
    return true
  }
}

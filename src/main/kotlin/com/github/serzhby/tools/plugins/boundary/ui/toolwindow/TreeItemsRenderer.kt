package com.github.serzhby.tools.plugins.boundary.ui.toolwindow

import com.github.serzhby.tools.plugins.boundary.BoundaryBundle
import com.github.serzhby.tools.plugins.boundary.model.AuthMethod
import com.github.serzhby.tools.plugins.boundary.model.BoundaryTarget
import com.github.serzhby.tools.plugins.boundary.model.Scope
import com.github.serzhby.tools.plugins.boundary.model.Session
import com.github.serzhby.tools.plugins.boundary.services.Host
import com.intellij.database.Dbms
import com.intellij.icons.AllIcons
import com.intellij.ui.ColoredTreeCellRenderer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.speedSearch.SpeedSearchUtil
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode

class TreeItemsRenderer : ColoredTreeCellRenderer() {
  override fun customizeCellRenderer(
    tree: JTree,
    value: Any?,
    selected: Boolean,
    expanded: Boolean,
    leaf: Boolean,
    row: Int,
    hasFocus: Boolean
  ) {
    val node = value as? DefaultMutableTreeNode ?: return
    when (node.userObject) {
      is Scope -> render(node.userObject as Scope)
      is BoundaryTarget -> render(node.userObject as BoundaryTarget)
      is Host -> render(node.userObject as Host)
      is Session -> render(node.userObject as Session)
      is AuthMethod -> render(node.userObject as AuthMethod)
      else -> SpeedSearchUtil.applySpeedSearchHighlighting(tree, this, true, selected)
    }
  }

  private fun render(scope: Scope) {
    append(scope.name ?: scope.type, SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES)
  }

  private fun render(target: BoundaryTarget) {
    val dbms = Dbms.fromString(target.name)
    icon = dbms.icon
    val style = if (dbms == Dbms.UNKNOWN) {
      SimpleTextAttributes.GRAYED_ATTRIBUTES
    } else {
      SimpleTextAttributes.REGULAR_ATTRIBUTES
    }
    append(target.name, style)
  }

  private fun render(host: Host) {
    append(host.url, SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES)
    append("  ${host.keyringType.type}", SimpleTextAttributes.GRAYED_ITALIC_ATTRIBUTES)
  }

  private fun render(session: Session) {
    icon = AllIcons.Actions.RunAll
    append(session.createdTime, SimpleTextAttributes.GRAYED_ATTRIBUTES)
  }

  private fun render(authMethod: AuthMethod) {
    append(BoundaryBundle.message("auth.with", authMethod.type), SimpleTextAttributes.REGULAR_ATTRIBUTES)
    append("  ${authMethod.name}", SimpleTextAttributes.GRAYED_ITALIC_ATTRIBUTES)
  }
}

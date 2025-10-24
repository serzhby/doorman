package com.github.serzhby.tools.plugins.doorman.actions

import com.github.serzhby.tools.plugins.doorman.model.KeyringType
import com.github.serzhby.tools.plugins.doorman.services.BoundarySettings
import com.github.serzhby.tools.plugins.doorman.services.Host
import com.github.serzhby.tools.plugins.doorman.ui.AddHostDialog
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class AddHostAction : AnAction(
  "Add a Host",
  "Add a boundary host",
  AllIcons.General.Add
) {
  override fun actionPerformed(e: AnActionEvent) {
    val project = e.project ?: return
    val dialog = AddHostDialog(project)
    if (dialog.showAndGet()) {
      val url = dialog.url
      val keyringType = KeyringType.fromType(dialog.keyringType)
      BoundarySettings.getInstance().state.apply {
        hosts = hosts + Host(url, keyringType)
      }
      RefreshItemsAction.trigger(project)
    }
  }
}
package com.github.serzhby.tools.plugins.boundary.ui

import com.github.serzhby.tools.plugins.boundary.BoundaryBundle
import com.github.serzhby.tools.plugins.boundary.model.KeyringType
import com.github.serzhby.tools.plugins.boundary.model.Platform
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.border.EmptyBorder

class AddHostDialog(project: Project) : DialogWrapper(project) {

  internal val urlField = JTextField(30)
  internal val keyringTypeBox = JComboBox(
    defineAvailableOptions().map { it.type }.toTypedArray()
  )

  val url: String
    get() = urlField.text.trim()

  val keyringType: String
    get() = keyringTypeBox.selectedItem as String

  init {
    title = BoundaryBundle.message("addHostDialog.title")
    setOKButtonText(BoundaryBundle.message("addHostDialog.save"))
    init()
  }

  fun defineAvailableOptions(): List<KeyringType> {
    val platform = Platform.Companion.currentPlatform
    return if (platform == Platform.NOT_SUPPORTED) {
      listOf(KeyringType.NONE)
    } else {
      KeyringType.values().filter { it.platforms.contains(platform) }
    }
  }

  override fun createCenterPanel(): JComponent {
    val panel = JPanel(GridBagLayout()).apply {
      border = EmptyBorder(10, 10, 10, 10)
    }
    val gbc = GridBagConstraints().apply {
      fill = GridBagConstraints.HORIZONTAL
      insets = Insets(4, 4, 4, 4)
    }

    gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0
    panel.add(JLabel(BoundaryBundle.message("addHostDialog.enterUrlLabel")), gbc)

    gbc.gridx = 1; gbc.weightx = 1.0
    panel.add(urlField, gbc)

    gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0
    panel.add(JLabel(BoundaryBundle.message("addHostDialog.keyringTypeLabel")), gbc)

    gbc.gridx = 1; gbc.weightx = 1.0
    panel.add(keyringTypeBox, gbc)

    return panel
  }

  override fun doValidate(): ValidationInfo? {
    return super.doValidate()
  }
}
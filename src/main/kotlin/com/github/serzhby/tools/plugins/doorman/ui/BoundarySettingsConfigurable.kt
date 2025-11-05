package com.github.serzhby.tools.plugins.doorman.ui

import com.github.serzhby.tools.plugins.doorman.BoundaryBundle
import com.github.serzhby.tools.plugins.doorman.services.BoundarySettings
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class BoundarySettingsConfigurable : Configurable {

  private var component: JPanel? = null
  private lateinit var executableField: TextFieldWithBrowseButton

  override fun getDisplayName(): String = BoundaryBundle.message("settings.title")

  override fun createComponent(): JComponent {
    val panel = JPanel(BorderLayout())

    executableField = TextFieldWithBrowseButton()
    val descriptor = FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor()
    descriptor.title = BoundaryBundle.message("settings.executableLabel")
    descriptor.description = BoundaryBundle.message("settings.executableDescription")
    executableField.addBrowseFolderListener(TextBrowseFolderListener(descriptor))

    val form = FormBuilder.createFormBuilder()
      .addLabeledComponent(JLabel(BoundaryBundle.message("settings.executableLabelText")), executableField, 1, false)
      .panel

    val content = JPanel(VerticalLayout(8))
    content.add(form)

    panel.add(content, BorderLayout.NORTH)

    component = panel
    reset()
    return panel
  }

  override fun isModified(): Boolean {
    val settings = BoundarySettings.getInstance()
    return executableField.text.trim() != settings.getBoundaryExecutable()
  }

  override fun apply() {
    val settings = BoundarySettings.getInstance()
    settings.setBoundaryExecutable(executableField.text.trim())
  }

  override fun reset() {
    val settings = BoundarySettings.getInstance()
    executableField.text = settings.getBoundaryExecutable()
  }

  override fun disposeUIResources() {
    component = null
  }
}

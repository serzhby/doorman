package com.github.serzhby.tools.plugins.doorman.services

import com.github.serzhby.tools.plugins.doorman.model.KeyringType
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

@State(
  name = "com.github.serzhby.tools.plugins.boundary.services.BoundarySetting",
  storages = [Storage("boundarySettings.xml")]
)
class BoundarySettings : PersistentStateComponent<BoundaryState> {

  private var state: BoundaryState = BoundaryState()

  override fun getState(): BoundaryState = state

  override fun loadState(state: BoundaryState) {
    this.state = state
  }

  companion object {
    fun getInstance(): BoundarySettings = service()
  }
}

data class BoundaryState(
  var hosts: List<Host> = emptyList()
) {
  constructor(): this(emptyList())
}

data class Host(
  var url: String,
  var keyringType: KeyringType
) {
  constructor(): this("", KeyringType.NONE)
}
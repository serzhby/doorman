package com.github.serzhby.tools.plugins.doorman.model

import com.github.serzhby.tools.plugins.doorman.model.Platform.LINUX
import com.github.serzhby.tools.plugins.doorman.model.Platform.MACOS
import com.github.serzhby.tools.plugins.doorman.model.Platform.WINDOWS

enum class KeyringType(val type: String, val platforms: List<Platform>) {
  WINCRED("wincred", listOf(WINDOWS)),
  KEYCHAIN("keychain", listOf(MACOS)),
  SECRET_SERVICE("secret-service", listOf(LINUX)),
  PASS("pass", listOf(LINUX)),
  NONE("none", listOf(LINUX, MACOS, WINDOWS));

  companion object {
    fun fromType(type: String): KeyringType {
      return values().find { it.type == type } ?: throw IllegalArgumentException("Unknown keyring type: $type")
    }
  }
}


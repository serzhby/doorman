package com.github.serzhby.tools.plugins.boundary.model

import java.util.*

enum class Platform {
  LINUX,
  MACOS,
  WINDOWS,
  NOT_SUPPORTED;

  companion object {
    val currentPlatform: Platform by lazy {
      val platformString = System.getProperty("os.name").lowercase(Locale.getDefault())
      with(platformString) {
        when {
          contains("win") -> WINDOWS
          contains("nix") -> LINUX
          contains("nux") -> LINUX
          contains("aix") -> LINUX
          contains("mac") -> MACOS
          else -> NOT_SUPPORTED
        }
      }
    }
  }
}
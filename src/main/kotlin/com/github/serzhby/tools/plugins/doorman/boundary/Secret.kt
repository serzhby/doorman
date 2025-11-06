package com.github.serzhby.tools.plugins.doorman.boundary

data class Secret(
  val raw: String,
  val decoded: DecodedSecret
)
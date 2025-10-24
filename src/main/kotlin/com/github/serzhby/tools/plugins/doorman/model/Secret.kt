package com.github.serzhby.tools.plugins.doorman.model

data class Secret(
  val raw: String,
  val decoded: DecodedSecret
)
package com.github.serzhby.tools.plugins.boundary.model

data class Secret(
  val raw: String,
  val decoded: DecodedSecret
)
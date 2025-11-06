package com.github.serzhby.tools.plugins.doorman.boundary

import com.fasterxml.jackson.annotation.JsonProperty

data class ListSessionsResult(
  val items: List<Session> = emptyList(),
  @JsonProperty("status_code")
  val statusCode: Int
)

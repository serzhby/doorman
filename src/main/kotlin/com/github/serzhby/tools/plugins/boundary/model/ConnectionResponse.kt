package com.github.serzhby.tools.plugins.boundary.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ConnectionResponse(
  val address: String,
  val port: Int,
  val protocol: String,
  val expiration: String,
  @JsonProperty("connection_limit")
  val connectionLimit: Int,
  @JsonProperty("session_id")
  val sessionId: String,
  val credentials: List<Credential>
)

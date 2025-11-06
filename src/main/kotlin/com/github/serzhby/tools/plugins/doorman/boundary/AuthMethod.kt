package com.github.serzhby.tools.plugins.doorman.boundary

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthMethod(
  val id: String,
  @JsonProperty("scope_id")
  val scopeId: String,
  val scope: Scope,
  val name: String,
  val description: String?,
  val type: String,
  @JsonProperty("is_primary")
  val isPrimary: Boolean
)
package com.github.serzhby.tools.plugins.doorman.boundary

import com.fasterxml.jackson.annotation.JsonProperty

data class Scope(
  val id: String,
  val type: String,
  val name: String?,
  val description: String?,
  @JsonProperty("parent_scope_id")
  val parentScopeId: String?
)
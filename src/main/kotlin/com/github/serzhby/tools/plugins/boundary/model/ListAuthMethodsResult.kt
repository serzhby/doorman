package com.github.serzhby.tools.plugins.boundary.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ListAuthMethodsResult(
  val items: List<AuthMethod> = emptyList(),
  @JsonProperty("status_code")
  val statusCode: Int
)

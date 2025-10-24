package com.github.serzhby.tools.plugins.boundary.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ListTargetResult(
  val items: List<BoundaryTarget> = emptyList(),
  @JsonProperty("status_code")
  val statusCode: Int
) {
  fun isOk(): Boolean = statusCode in 200..299
}

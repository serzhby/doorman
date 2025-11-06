package com.github.serzhby.tools.plugins.doorman.boundary

import com.fasterxml.jackson.annotation.JsonProperty

data class CloseSessionResult(
  val context: String?,
  @JsonProperty("status_code")
  val statusCode: Int
) {
  fun isOk(): Boolean = statusCode in 200..299
}

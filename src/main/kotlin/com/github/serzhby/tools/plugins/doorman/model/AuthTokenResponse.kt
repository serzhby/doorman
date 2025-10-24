package com.github.serzhby.tools.plugins.doorman.model

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthTokenResponse(
  @JsonProperty("status_code")
  val statusCode: Int,
  val item: AuthTokenItem? = null
) {
  fun isOk(): Boolean = statusCode in 200..299
}
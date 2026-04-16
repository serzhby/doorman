package com.github.serzhby.tools.plugins.doorman.boundary

import com.fasterxml.jackson.annotation.JsonProperty

class ListAuthMethodsResult(
  items: List<AuthMethod>?,
  @JsonProperty("status_code")
  val statusCode: Int
) {
  val items = items ?: emptyList()

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as ListAuthMethodsResult

    if (statusCode != other.statusCode) return false
    if (items != other.items) return false

    return true
  }

  override fun hashCode(): Int {
    var result = statusCode
    result = 31 * result + items.hashCode()
    return result
  }

  override fun toString(): String {
    return "ListAuthMethodsResult(statusCode=$statusCode, items=$items)"
  }
}

package com.github.serzhby.tools.plugins.doorman.boundary

import com.fasterxml.jackson.annotation.JsonProperty

class ListSessionsResult(
  items: List<Session>?,
  @JsonProperty("status_code")
  val statusCode: Int
) {
  val items = items ?: emptyList()

  override fun toString(): String {
    return "ListSessionsResult(statusCode=$statusCode, items=$items)"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as ListSessionsResult

    if (statusCode != other.statusCode) return false
    if (items != other.items) return false

    return true
  }

  override fun hashCode(): Int {
    var result = statusCode
    result = 31 * result + items.hashCode()
    return result
  }
}

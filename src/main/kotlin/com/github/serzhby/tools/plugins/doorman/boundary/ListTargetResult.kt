package com.github.serzhby.tools.plugins.doorman.boundary

import com.fasterxml.jackson.annotation.JsonProperty

class ListTargetResult(
  items: List<BoundaryTarget>?,
  @JsonProperty("status_code")
  val statusCode: Int
) {
  fun isOk(): Boolean = statusCode in 200..299

  override fun toString(): String {
    return "ListTargetResult(items=$items, statusCode=$statusCode)"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as ListTargetResult

    if (statusCode != other.statusCode) return false
    if (items != other.items) return false

    return true
  }

  override fun hashCode(): Int {
    var result = statusCode
    result = 31 * result + items.hashCode()
    return result
  }

  val items = items ?: emptyList()


}

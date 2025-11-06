package com.github.serzhby.tools.plugins.doorman.boundary

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthTokenAttributes(
  @JsonProperty("account_id")
  val accountId: String,
  @JsonProperty("approximate_last_used_time")
  val approximateLastUsedTime: String,
  @JsonProperty("auth_method_id")
  val authMethodId: String,
  @JsonProperty("created_time")
  val createdTime: String,
  @JsonProperty("expiration_time")
  val expirationTime: String,
  val id: String,
  val scope: Scope,
  val token: String,
  @JsonProperty("updated_time")
  val updatedTime: String,
  @JsonProperty("user_id")
  val userId: String
)
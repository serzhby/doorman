package com.github.serzhby.tools.plugins.doorman.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Session(
  val id: String,
  @JsonProperty("target_id") val targetId: String,
  val scope: Scope,
  @JsonProperty("created_time") val createdTime: String,
  @JsonProperty("updated_time") val updatedTime: String,
  val version: Int,
  val type: String,
  @JsonProperty("expiration_time") val expirationTime: String,
  @JsonProperty("auth_token_id") val authTokenId: String,
  @JsonProperty("user_id") val userId: String,
  @JsonProperty("host_set_id") val hostSetId: String,
  @JsonProperty("host_id") val hostId: String,
  @JsonProperty("scope_id") val scopeId: String,
  val endpoint: String,
  val status: String
)
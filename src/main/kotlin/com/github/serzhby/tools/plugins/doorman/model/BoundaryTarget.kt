package com.github.serzhby.tools.plugins.doorman.model

import com.fasterxml.jackson.annotation.JsonProperty

data class BoundaryTarget(
  val id: String,
  @JsonProperty("scope_id")
  val scopeId: String,
  val scope: Scope,
  val name: String,
  val description: String,
  @JsonProperty("created_time")
  val createdTime: String,
  @JsonProperty("updated_time")
  val updatedTime: String,
  val version: Int,
  val type: String,
  @JsonProperty("session_max_seconds")
  val sessionMaxSeconds: Int,
  @JsonProperty("session_connection_limit")
  val sessionConnectionLimit: Int,
  @JsonProperty("egress_worker_filter")
  val egressWorkerFilter: String,
  val attributes: Map<String, Object?>,
  @JsonProperty("authorized_actions")
  val authorizedActions: List<String>,
  val address: String
)
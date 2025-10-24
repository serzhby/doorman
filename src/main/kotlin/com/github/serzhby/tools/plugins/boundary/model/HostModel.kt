package com.github.serzhby.tools.plugins.boundary.model

import com.github.serzhby.tools.plugins.boundary.services.Host

data class HostModel(
  val host: Host,
  val targets: List<BoundaryTarget> = emptyList(),
  val sessions: List<Session> = emptyList(),
  val authMethods: List<AuthMethod> = emptyList()
)

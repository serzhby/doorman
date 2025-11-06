package com.github.serzhby.tools.plugins.doorman.boundary

import com.github.serzhby.tools.plugins.doorman.services.Host

data class HostModel(
  val host: Host,
  val targets: List<BoundaryTarget> = emptyList(),
  val sessions: List<Session> = emptyList(),
  val authMethods: List<AuthMethod> = emptyList()
)

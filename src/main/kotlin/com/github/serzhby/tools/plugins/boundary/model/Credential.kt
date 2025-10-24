package com.github.serzhby.tools.plugins.boundary.model

data class Credential(
  val credentialSource: CredentialSource?,
  val secret: Secret
)
package com.github.serzhby.tools.plugins.doorman.boundary

data class Credential(
  val credentialSource: CredentialSource?,
  val secret: Secret
)
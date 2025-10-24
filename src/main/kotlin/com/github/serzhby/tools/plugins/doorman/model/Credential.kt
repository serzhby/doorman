package com.github.serzhby.tools.plugins.doorman.model

data class Credential(
  val credentialSource: CredentialSource?,
  val secret: Secret
)
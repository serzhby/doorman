package com.github.serzhby.tools.plugins.doorman.boundary

data class CredentialSource(
  val id: String,
  val name: String?,
  val description: String?,
  val credentialStoreId: String,
  val type: String
)
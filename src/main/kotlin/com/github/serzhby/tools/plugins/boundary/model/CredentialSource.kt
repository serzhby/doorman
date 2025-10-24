package com.github.serzhby.tools.plugins.boundary.model

data class CredentialSource(
  val id: String,
  val name: String?,
  val description: String?,
  val credentialStoreId: String,
  val type: String
)
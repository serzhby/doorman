package com.github.serzhby.tools.plugins.doorman.services

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.serzhby.tools.plugins.doorman.model.AuthMethod
import com.github.serzhby.tools.plugins.doorman.model.AuthTokenResponse
import com.github.serzhby.tools.plugins.doorman.model.BoundaryTarget
import com.github.serzhby.tools.plugins.doorman.model.CloseSessionResult
import com.github.serzhby.tools.plugins.doorman.model.ConnectionResponse
import com.github.serzhby.tools.plugins.doorman.model.KeyringType
import com.github.serzhby.tools.plugins.doorman.model.ListAuthMethodsResult
import com.github.serzhby.tools.plugins.doorman.model.ListSessionsResult
import com.github.serzhby.tools.plugins.doorman.model.ListTargetResult
import com.github.serzhby.tools.plugins.doorman.model.Session
import com.intellij.openapi.components.Service

@Service(Service.Level.APP)
class BoundaryService {

  private val objectMapper = jacksonObjectMapper()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  private val externalCommandExecutor = ExternalCommandExecutor()

  private val tokenStore = InMemoryTokenStore.instance

  fun authenticate(host: Host, authMethod: AuthMethod) {
    if (authMethod.type == "oidc") {
      val format = if (host.keyringType.type == KeyringType.NONE.type) "json" else "table"
      val command = listOf(
        "boundary", "authenticate", authMethod.type,
        "-auth-method-id", authMethod.id,
        "-format", format,
        "-keyring-type", host.keyringType.type,
        "-addr", host.url
      )
      val output = externalCommandExecutor.executeForOutput(
        command = command,
        env = emptyMap(),
        timeoutMillis =  5 * 60 * 1000,
        destroyOnTimeout = true,
        predicate = { false }
      )
      if (host.keyringType == KeyringType.NONE) {
        val response = objectMapper.readValue<AuthTokenResponse>(output)
        if (response.isOk()) {
          tokenStore[host] = response.item ?: throw IllegalStateException("Authentication token is null")
        }
      }
    } else {
      throw IllegalArgumentException("Unsupported authentication method: ${authMethod.type}. Only 'oidc' is supported.")
    }
  }

  fun listResources(host: Host): ListTargetResult {
    val command = listOfNotNull(
      "boundary", "targets", "list",
      "-recursive",
      "-format", "json",
      "-keyring-type", host.keyringType.type,
      "-addr", host.url
    )
    return execute(buildCommand(host, command), buildEnv(host))
  }

  fun listSessions(host: Host): ListSessionsResult {
    val command = listOf(
      "boundary", "sessions", "list", "-recursive",
      "-format", "json",
      "-filter", "item.status==\"active\"",
      "-keyring-type", host.keyringType.type,
      "-addr", host.url
    )
    return execute(buildCommand(host, command), buildEnv(host))
  }

  fun closeSession(host: Host, session: Session): CloseSessionResult {
    val command = listOf(
      "boundary", "sessions", "cancel",
      "-id", session.id,
      "-format", "json",
      "-keyring-type", host.keyringType.type,
      "-addr", host.url
    )
    return execute(buildCommand(host, command), buildEnv(host))
  }

  fun listAuthMethods(host: Host): ListAuthMethodsResult {
    val command = listOf(
      "boundary", "auth-methods", "list", "-recursive",
      "-format", "json",
      "-keyring-type", "none",
      "-addr", host.url
    )
    return execute(command, buildEnv(host))
  }

  fun createConnection(host: Host, target: BoundaryTarget): ConnectionResponse? {
    val command = listOf(
      "boundary", "connect", target.id,
      "-format", "json",
      "-keyring-type", host.keyringType.type,
      "-addr", host.url
    )
    return executeIndefinite(buildCommand(host, command), buildEnv(host))
  }

  private fun buildCommand(host: Host, command: List<String>): List<String> {
    return if (host.keyringType == KeyringType.NONE) {
      command + listOf("-token", "env://BOUNDARY_TOKEN")
    } else {
      command
    }
  }

  private fun buildEnv(host: Host): Map<String, String> {
    val token = tokenStore[host]?.attributes?.token
    return if (token != null) {
      return mapOf(
        "BOUNDARY_TOKEN" to token
      )
    } else {
      emptyMap()
    }
  }

  private inline fun <reified T> execute(command: List<String>, env: Map<String, String>, timeoutMillis: Int = TIMEOUT_MILLIS): T {
    return execute(command, env, true, timeoutMillis) { false }
  }

  private inline fun <reified T> executeIndefinite(command: List<String>, env: Map<String, String>): T {
    return execute(command, env, false) { runCatching { objectMapper.readTree(it) }.isSuccess }
  }

  private inline fun <reified T> execute(
    command: List<String>,
    env: Map<String, String>,
    destroyOnTimeout: Boolean,
    timeoutMillis: Int = TIMEOUT_MILLIS,
    noinline predicate: (String) -> Boolean
  ): T {
    val output = externalCommandExecutor.executeForOutput(command, env, timeoutMillis, destroyOnTimeout, predicate)
    val json = output.lines().filter { it.trim().startsWith("[") || it.trim().startsWith("{") }.joinToString("\n")
    return objectMapper.readValue<T>(json)
  }

  private companion object {
    private const val TIMEOUT_MILLIS = 60000
  }

}
package com.github.serzhby.tools.plugins.doorman.services

import com.github.serzhby.tools.plugins.doorman.model.AuthTokenItem
import java.util.concurrent.ConcurrentHashMap

class InMemoryTokenStore : TokenStore {

  private val tokenStore = ConcurrentHashMap<Host, AuthTokenItem>()

  override operator fun set(host: Host, token: AuthTokenItem) {
    tokenStore[host] = token
  }

  override operator fun get(host: Host): AuthTokenItem? {
    return tokenStore[host]
  }

  companion object {
    val instance: InMemoryTokenStore by lazy { InMemoryTokenStore() }
  }
}
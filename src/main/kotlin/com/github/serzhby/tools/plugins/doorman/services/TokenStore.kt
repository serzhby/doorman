package com.github.serzhby.tools.plugins.doorman.services

import com.github.serzhby.tools.plugins.doorman.boundary.AuthTokenItem

interface TokenStore {
  operator fun set(host: Host, token: AuthTokenItem)
  operator fun get(host: Host): AuthTokenItem?
}
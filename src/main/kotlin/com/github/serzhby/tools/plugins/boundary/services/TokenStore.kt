package com.github.serzhby.tools.plugins.boundary.services

import com.github.serzhby.tools.plugins.boundary.model.AuthTokenItem

interface TokenStore {
  operator fun set(host: Host, token: AuthTokenItem)
  operator fun get(host: Host): AuthTokenItem?
}
package com.github.serzhby.tools.plugins.doorman.boundary.exceptions

import com.github.serzhby.tools.plugins.doorman.boundary.AuthMethod

class UnsupportedAuthMethodException(
  val authMethod: AuthMethod
) : BoundaryException()
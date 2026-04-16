package com.github.serzhby.tools.plugins.doorman.services

import com.intellij.openapi.diagnostic.debug
import com.intellij.openapi.diagnostic.thisLogger
import java.io.BufferedReader
import java.lang.Thread.sleep
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class ExternalCommandExecutor {


  fun executeForOutput(
    command: List<String>,
    env: Map<String, String>,
    timeoutMillis: Int,
    destroyOnTimeout: Boolean,
    predicate: (String) -> Boolean
  ): String {
    val logger = thisLogger()
    logger.debug { "Executing command: ${command.joinToString(" ")}, env: $env, timeoutMillis: $timeoutMillis, destroyOnTimeout: $destroyOnTimeout" }
    val process = startProcess(command, env)
    val ready = CompletableFuture<String>()
    thread(isDaemon = true, name = "proc-reader") {
      val buffer = StringBuilder()
      BufferedReader(process.inputStream.reader()).use { br ->
        while (br.readLine().also { buffer.appendLine(it) } != null) {
          if (predicate(buffer.toString())) {
            ready.complete(buffer.toString())
            return@thread
          }
        }
      }
      if (!ready.isDone) {
        ready.complete(buffer.toString())
      }
    }
    thread(isDaemon = true, name = "proc-waiter") {
      val finished = process.waitFor(timeoutMillis.toLong(), TimeUnit.MILLISECONDS)
      if (!finished) {
        logger.debug { "Command timed out after $timeoutMillis ms: ${command.joinToString(" ")}" }
      }
      if (!finished && destroyOnTimeout) {
        process.destroy()
        if (!process.waitFor(200, TimeUnit.MILLISECONDS)) {
          logger.warn("Process did not terminate after timeout, forcefully destroying it: $command")
          process.destroyForcibly()
        }
      }
      sleep(1000)
      if (!ready.isDone) {
        ready.complete(process.inputStream.bufferedReader().readText())
      }
    }
    val output = ready.get()
    logger.debug { "Command output (${output.length} chars): $output" }
    return output
  }

  private fun startProcess(command: List<String>, env: Map<String, String>): Process {
    val processBuilder = ProcessBuilder(command)
      .redirectErrorStream(true)
    processBuilder.environment().putAll(env)
    return processBuilder.start()
  }

}
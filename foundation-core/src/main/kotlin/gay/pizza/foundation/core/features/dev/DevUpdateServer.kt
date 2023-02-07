package gay.pizza.foundation.core.features.dev

import com.charleskorn.kaml.Yaml
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import gay.pizza.foundation.shared.copyDefaultConfig
import gay.pizza.foundation.core.FoundationCorePlugin
import gay.pizza.foundation.core.features.update.UpdateService
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.jsonPrimitive
import java.net.InetSocketAddress
import kotlin.io.path.inputStream

class DevUpdateServer(val plugin: FoundationCorePlugin) {
  private lateinit var config: DevUpdateConfig
  private var server: HttpServer? = null

  private val json = Json {
    prettyPrint = true
    prettyPrintIndent = "  "
    ignoreUnknownKeys = true
  }

  fun enable() {
    val configPath = copyDefaultConfig<FoundationCorePlugin>(
      plugin.slF4JLogger,
      plugin.pluginDataPath,
      "devupdate.yaml"
    )

    config = Yaml.default.decodeFromStream(DevUpdateConfig.serializer(), configPath.inputStream())
    start()
  }

  private fun start() {
    if (config.token.isEmpty()) {
      return
    }

    if (config.token.length < 8) {
      plugin.slF4JLogger.warn("DevUpdateServer Token was too short (must be 8 or more characters)")
      return
    }

    val server = HttpServer.create()
    server.createContext("/").setHandler { exchange ->
      handle(exchange)
    }
    server.bind(InetSocketAddress("0.0.0.0", config.port), 0)
    server.start()
    this.server = server
    plugin.slF4JLogger.info("DevUpdateServer listening on port ${config.port}")
  }

  private fun handle(exchange: HttpExchange) {
    val ip = exchange.remoteAddress.address.hostAddress
    if (!config.ipAllowList.contains("*") && !config.ipAllowList.contains(ip)) {
      plugin.slF4JLogger.warn("DevUpdateServer received request from IP $ip which is not allowed.")
      exchange.close()
      return
    }

    plugin.slF4JLogger.info("DevUpdateServer Request $ip ${exchange.requestMethod} ${exchange.requestURI.path}")
    if (exchange.requestMethod != "POST") {
      exchange.respond(405, "Method not allowed.")
      return
    }

    if (exchange.requestURI.path != "/webhook/update") {
      exchange.respond(404, "Not Found.")
      return
    }

    if (exchange.requestURI.query != config.token) {
      exchange.respond(401, "Unauthorized.")
      return
    }

    val payload: DevUpdatePayload
    try {
      payload = json.decodeFromStream(exchange.requestBody)
    } catch (e: Exception) {
      plugin.slF4JLogger.error("Failed to decode request body.", e)
      exchange.respond(400, "Bad Request")
      return
    }

    if (payload.objectKind != "pipeline" ||
      payload.objectAttributes["ref"]?.jsonPrimitive?.content != "main" ||
      payload.objectAttributes["status"]?.jsonPrimitive?.content != "success"
    ) {
      exchange.respond(200, "Event was not relevant for update.")
      return
    }

    exchange.respond(200, "Success.")
    plugin.slF4JLogger.info("DevUpdate Started")
    UpdateService.updatePlugins(plugin.server.consoleSender) {
      plugin.server.scheduler.runTask(plugin) { ->
        plugin.server.shutdown()
      }
    }
  }

  fun disable() {
    server?.stop(1)
  }

  private fun HttpExchange.respond(code: Int, content: String) {
    val encoded = content.encodeToByteArray()
    sendResponseHeaders(code, encoded.size.toLong())
    responseBody.write(encoded)
    responseBody.close()
    close()
  }
}

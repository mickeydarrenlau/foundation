package cloud.kubelet.foundation.core.devupdate

import cloud.kubelet.foundation.core.FoundationCorePlugin
import cloud.kubelet.foundation.core.Util
import com.charleskorn.kaml.Yaml
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import kotlin.io.path.inputStream

class DevUpdateServer(val plugin: FoundationCorePlugin) {
  private lateinit var config: DevUpdateConfig
  private var server: HttpServer? = null

  fun enable() {
    val configPath = Util.copyDefaultConfig<FoundationCorePlugin>(
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
      plugin.slF4JLogger.warn("DevUpdate Token was too short (must be 8 or more characters)")
      return
    }

    val server = HttpServer.create()
    server.createContext("/").setHandler { exchange ->
      val ip = exchange.remoteAddress.address.hostAddress
      if (!config.ipAllowList.contains("*") && !config.ipAllowList.contains(ip)) {
        plugin.slF4JLogger.warn("DevUpdate Server received request from IP $ip which is not allowed.")
        exchange.close()
        return@setHandler
      }

      plugin.slF4JLogger.info("DevUpdate Server Request $ip ${exchange.requestMethod} ${exchange.requestURI.path}")
      if (exchange.requestMethod != "POST") {
        exchange.respond(405, "Method not allowed.")
        return@setHandler
      }

      if (exchange.requestURI.path != "/webhook/update") {
        exchange.respond(404, "Not Found.")
        return@setHandler
      }

      if (exchange.requestURI.query != config.token) {
        exchange.respond(401, "Unauthorized.")
        return@setHandler
      }

      exchange.respond(200, "Success.")
      plugin.server.scheduler.runTask(plugin) { ->
        plugin.slF4JLogger.info("DevUpdate Server Restart")
        try {
          plugin.server.dispatchCommand(plugin.server.consoleSender, "fupdate")
          plugin.server.dispatchCommand(plugin.server.consoleSender, "stop")
        } catch (e: Exception) {
          plugin.slF4JLogger.error("DevUpdate Server failed to update server.", e)
        }
      }
    }
    server.bind(InetSocketAddress("0.0.0.0", config.port), 0)
    server.start()
    this.server = server
    plugin.slF4JLogger.info("DevUpdate Server listening on port ${config.port}")
  }

  fun disable() {
    server?.stop(0)
  }

  private fun HttpExchange.respond(code: Int, content: String) {
    val encoded = content.encodeToByteArray()
    sendResponseHeaders(code, encoded.size.toLong())
    responseBody.write(encoded)
    responseBody.close()
    close()
  }
}

package cloud.kubelet.foundation.heimdall

import cloud.kubelet.foundation.core.FoundationCorePlugin
import cloud.kubelet.foundation.core.Util
import cloud.kubelet.foundation.heimdall.buffer.BufferFlushThread
import cloud.kubelet.foundation.heimdall.buffer.EventBuffer
import cloud.kubelet.foundation.heimdall.event.PlayerPositionEvent
import cloud.kubelet.foundation.heimdall.model.HeimdallConfig
import cloud.kubelet.foundation.heimdall.table.PlayerPositionTable
import com.charleskorn.kaml.Yaml
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.Driver
import java.lang.Exception
import java.time.Instant
import kotlin.io.path.inputStream

class FoundationHeimdallPlugin : JavaPlugin(), Listener {
  private lateinit var config: HeimdallConfig
  private lateinit var pool: HikariDataSource
  internal lateinit var db: Database

  private val buffer = EventBuffer()
  private val bufferFlushThread = BufferFlushThread(this, buffer)

  override fun onEnable() {
    val foundation = server.pluginManager.getPlugin("Foundation") as FoundationCorePlugin

    val configPath = Util.copyDefaultConfig<FoundationHeimdallPlugin>(
      slF4JLogger,
      foundation.pluginDataPath,
      "heimdall.yaml"
    )
    config = Yaml.default.decodeFromStream(HeimdallConfig.serializer(), configPath.inputStream())
    if (!config.enabled) {
      slF4JLogger.info("Heimdall is not enabled.")
      return
    }
    slF4JLogger.info("Heimdall is enabled.")
    if (!Driver.isRegistered()) {
      Driver.register()
    }
    pool = HikariDataSource(HikariConfig().apply {
      jdbcUrl = config.db.url
      username = config.db.username
      password = config.db.password
      schema = "heimdall"
    })
    val initMigrationContent = FoundationHeimdallPlugin::class.java.getResourceAsStream(
      "/init.sql"
    )?.readAllBytes()?.decodeToString() ?: throw RuntimeException("Unable to find Heimdall init.sql")

    val statements = initMigrationContent.sqlSplitStatements()

    pool.connection.use { conn ->
      conn.autoCommit = false
      try {
        for (statementAsString in statements) {
          conn.prepareStatement(statementAsString).use {
            it.execute()
          }
        }
        conn.commit()
      } catch (e: Exception) {
        conn.rollback()
        throw e
      } finally {
        conn.autoCommit = true
      }
    }

    db = Database.connect(pool)
    server.pluginManager.registerEvents(this, this)
    bufferFlushThread.start()
  }

  @EventHandler
  fun onPlayerMove(event: PlayerMoveEvent) = buffer.push(PlayerPositionEvent(event))

  override fun onDisable() {
    bufferFlushThread.stop()
  }
}

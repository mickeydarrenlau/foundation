package gay.pizza.foundation.core.features.player

import com.charleskorn.kaml.Yaml
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.cache.RemovalCause
import gay.pizza.foundation.core.FoundationCorePlugin
import gay.pizza.foundation.core.Util
import gay.pizza.foundation.core.abstraction.Feature
import net.kyori.adventure.text.Component
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.koin.core.component.inject
import java.time.Duration
import kotlin.io.path.inputStream

class PlayerFeature : Feature() {
  private val config by inject<PlayerConfig>()
  private lateinit var playerActivity: Cache<String, String>

  override fun enable() {
    playerActivity = CacheBuilder.newBuilder()
      .expireAfterWrite(Duration.ofSeconds(config.antiIdle.idleDuration.toLong()))
      .removalListener<String, String> z@{
        if (!config.antiIdle.enabled) return@z
        if (it.cause == RemovalCause.EXPIRED) {
          if (!config.antiIdle.ignore.contains(it.key!!)) {
            plugin.server.scheduler.runTask(plugin) { ->
              plugin.server.getPlayer(it.key!!)
                ?.kick(Component.text("Kicked for idling"), PlayerKickEvent.Cause.IDLING)
            }
          }
        }
      }.build()

    // Expire player activity tokens occasionally.
    plugin.server.scheduler.scheduleSyncRepeatingTask(plugin, {
      playerActivity.cleanUp()
    }, 20, 100)

    registerCommandExecutor(listOf("survival", "s"), GamemodeCommand(GameMode.SURVIVAL))
    registerCommandExecutor(listOf("creative", "c"), GamemodeCommand(GameMode.CREATIVE))
    registerCommandExecutor(listOf("adventure", "a"), GamemodeCommand(GameMode.ADVENTURE))
    registerCommandExecutor(listOf("spectator", "sp"), GamemodeCommand(GameMode.SPECTATOR))
    registerCommandExecutor(listOf("localweather", "lw"), LocalWeatherCommand())
    registerCommandExecutor(listOf("goose", "the_most_wonderful_kitty_ever"), GooseCommand())
  }

  override fun module() = org.koin.dsl.module {
    single {
      val configPath = Util.copyDefaultConfig<FoundationCorePlugin>(
        plugin.slF4JLogger,
        plugin.pluginDataPath,
        "player.yaml",
      )
      return@single Yaml.default.decodeFromStream(
        PlayerConfig.serializer(),
        configPath.inputStream()
      )
    }
  }

  @EventHandler
  private fun onPlayerJoin(e: PlayerJoinEvent) {
    if (!config.antiIdle.enabled) return

    playerActivity.put(e.player.name, e.player.name)
  }

  @EventHandler
  private fun onPlayerQuit(e: PlayerQuitEvent) {
    if (!config.antiIdle.enabled) return

    playerActivity.invalidate(e.player.name)
  }

  @EventHandler
  private fun onPlayerMove(e: PlayerMoveEvent) {
    if (!config.antiIdle.enabled) return

    if (e.hasChangedPosition() || e.hasChangedOrientation()) {
      playerActivity.put(e.player.name, e.player.name)
    }
  }
}

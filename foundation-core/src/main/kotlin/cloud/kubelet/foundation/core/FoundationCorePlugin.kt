package cloud.kubelet.foundation.core

import cloud.kubelet.foundation.core.command.*
import cloud.kubelet.foundation.core.devupdate.DevUpdateServer
import cloud.kubelet.foundation.core.persist.PersistentStore
import cloud.kubelet.foundation.core.persist.setAllProperties
import io.papermc.paper.event.player.AsyncChatEvent
import kotlinx.serialization.ExperimentalSerializationApi
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.GameMode
import org.bukkit.command.CommandExecutor
import org.bukkit.command.TabCompleter
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.nio.file.Path
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@ExperimentalSerializationApi
class FoundationCorePlugin : JavaPlugin(), Listener {
  internal val persistentStores = ConcurrentHashMap<String, PersistentStore>()
  private lateinit var _pluginDataPath: Path
  private lateinit var chatLogStore: PersistentStore
  private lateinit var devUpdateServer: DevUpdateServer

  var pluginDataPath: Path
    /**
     * Data path of the core plugin.
     * Can be used as a sanity check of sorts for dependencies to be sure the plugin is loaded.
     */
    get() {
      if (!::_pluginDataPath.isInitialized) {
        throw Exception("FoundationCore is not loaded!")
      }
      return _pluginDataPath
    }
    private set(value) {
      _pluginDataPath = value
    }

  /**
   * Fetch a persistent store by name. Make sure the name is path-safe, descriptive and consistent across server runs.
   */
  fun getPersistentStore(name: String) = persistentStores.getOrPut(name) { PersistentStore(this, name) }

  override fun onEnable() {
    pluginDataPath = dataFolder.toPath()
    val backupPath = pluginDataPath.resolve(BACKUPS_DIRECTORY)

    // Create Foundation plugin directories.
    pluginDataPath.toFile().mkdir()
    backupPath.toFile().mkdir()

    // Register this as an event listener.
    server.pluginManager.registerEvents(this, this)

    // Register commands.
    registerCommandExecutor("fbackup", BackupCommand(this, backupPath))
    registerCommandExecutor("fupdate", UpdateCommand())
    registerCommandExecutor(listOf("survival", "s"), GamemodeCommand(GameMode.SURVIVAL))
    registerCommandExecutor(listOf("creative", "c"), GamemodeCommand(GameMode.CREATIVE))
    registerCommandExecutor(listOf("adventure", "a"), GamemodeCommand(GameMode.ADVENTURE))
    registerCommandExecutor(listOf("spectator", "sp"), GamemodeCommand(GameMode.SPECTATOR))
    registerCommandExecutor(listOf("leaderboard", "lb"), LeaderboardCommand())
    registerCommandExecutor("pstore", PersistentStoreCommand(this))
    registerCommandExecutor("setspawn", SetSpawnCommand())
    registerCommandExecutor("spawn", SpawnCommand())

    val log = slF4JLogger
    log.info("Features:")
    Util.printFeatureStatus(log, "Backup", BACKUP_ENABLED)
    chatLogStore = getPersistentStore("chat-logs")
    devUpdateServer = DevUpdateServer(this)
    devUpdateServer.enable()
  }

  override fun onDisable() {
    persistentStores.values.forEach { store -> store.close() }
    persistentStores.clear()
    devUpdateServer.disable()
  }

  private fun registerCommandExecutor(name: String, executor: CommandExecutor) {
    registerCommandExecutor(listOf(name), executor)
  }

  private fun registerCommandExecutor(names: List<String>, executor: CommandExecutor) {
    for (name in names) {
      val command = getCommand(name) ?: throw Exception("Failed to get $name command")
      command.setExecutor(executor)
      if (executor is TabCompleter) {
        command.tabCompleter = executor
      }
    }
  }

  // TODO: Disabling chat reformatting until I do something with it and figure out how to make it
  //  be less disruptive.
  /*@EventHandler
  private fun onChatMessage(e: ChatEvent) {
    return
    e.isCancelled = true
    val name = e.player.displayName()
    val component = Component.empty()
      .append(leftBracket)
      .append(name)
      .append(rightBracket)
      .append(Component.text(' '))
      .append(e.message())
    server.sendMessage(component)
  }*/

  @EventHandler
  private fun logOnChatMessage(e: AsyncChatEvent) {
    val player = e.player
    val message = e.message()

    if (message !is TextComponent) {
      return
    }

    val content = message.content()
    chatLogStore.create("ChatMessageEvent") {
      setAllProperties(
        "timestamp" to Instant.now().toEpochMilli(),
        "player.id" to player.identity().uuid().toString(),
        "player.name" to player.name,
        "message.content" to content
      )
    }
  }

  companion object {
    private const val BACKUPS_DIRECTORY = "backups"

    private val leftBracket: Component = Component.text('[')
    private val rightBracket: Component = Component.text(']')

    const val BACKUP_ENABLED = true
  }
}

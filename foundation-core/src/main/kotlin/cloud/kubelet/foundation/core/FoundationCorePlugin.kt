package cloud.kubelet.foundation.core

import cloud.kubelet.foundation.core.command.BackupCommand
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.nio.file.Path

class FoundationCorePlugin : JavaPlugin(), Listener {
  private lateinit var _pluginDataPath: Path

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

    val log = slF4JLogger
    log.info("Features:")
    Util.printFeatureStatus(log, "Backup", BACKUP_ENABLED)
  }

  private fun registerCommandExecutor(name: String, executor: CommandExecutor) {
    val command = getCommand(name) ?: throw Exception("Failed to get $name command")
    command.setExecutor(executor)
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

  companion object {
    private const val BACKUPS_DIRECTORY = "backups"

    private val leftBracket: Component = Component.text('[')
    private val rightBracket: Component = Component.text(']')

    const val BACKUP_ENABLED = true
  }
}
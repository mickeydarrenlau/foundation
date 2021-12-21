package cloud.kubelet.foundation

import cloud.kubelet.foundation.command.BackupCommand
import io.papermc.paper.event.player.ChatEvent
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandExecutor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class Foundation : JavaPlugin(), Listener {
  override fun onEnable() {
    val dataPath = dataFolder.toPath()
    val backupPath = dataPath.resolve(BACKUPS_DIRECTORY)

    // Create Foundation plugin directories.
    dataPath.toFile().mkdir()
    backupPath.toFile().mkdir()

    // Register this as an event listener.
    server.pluginManager.registerEvents(this, this)

    // Register commands.
    registerCommandExecutor("fbackup", BackupCommand(this, backupPath))

    val log = slF4JLogger
    log.info("Features:")
    Util.printFeatureStatus(log, "Backup: ", BACKUP_ENABLED)
  }

  private fun registerCommandExecutor(name: String, executor: CommandExecutor) {
    val command = getCommand(name) ?: throw Exception("Failed to get $name command")
    command.setExecutor(executor)
  }

  @EventHandler
  private fun onChatMessage(e: ChatEvent) {
    e.isCancelled = true
    val name = e.player.displayName()
    val component = Component.empty()
      .append(leftBracket)
      .append(name)
      .append(rightBracket)
      .append(Component.text(' '))
      .append(e.message())
    server.sendMessage(component)
  }

  companion object {
    private const val BACKUPS_DIRECTORY = "backups"

    private val leftBracket: Component = Component.text('[')
    private val rightBracket: Component = Component.text(']')

    const val BACKUP_ENABLED = true
  }
}
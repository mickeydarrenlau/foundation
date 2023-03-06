package gay.pizza.foundation.heimdall.plugin.export

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin

class ExportWorldLoadCommand(private val plugin: Plugin) : CommandExecutor {
  override fun onCommand(
    sender: CommandSender,
    command: Command,
    label: String,
    args: Array<out String>
  ): Boolean {
    sender.sendMessage("Exporting all worlds...")
    plugin.slF4JLogger.info("Exporting all worlds")
    val export = WorldLoadExporter()
    for (world in sender.server.worlds) {
      export.exportLoadedChunks(world)
    }
    export.save()
    sender.sendMessage("Exported all worlds...")
    plugin.slF4JLogger.info("Exported all worlds")
    return true
  }
}

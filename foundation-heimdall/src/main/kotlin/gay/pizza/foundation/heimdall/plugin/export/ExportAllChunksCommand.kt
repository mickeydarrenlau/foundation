package gay.pizza.foundation.heimdall.plugin.export

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin

class ExportAllChunksCommand(private val plugin: Plugin) : CommandExecutor {
  override fun onCommand(
    sender: CommandSender,
    command: Command,
    label: String,
    args: Array<out String>
  ): Boolean {
    sender.sendMessage("Exporting all chunks...")
    plugin.slF4JLogger.info("Exporting all chunks")
    val export = ChunkExporter(plugin)
    for (world in sender.server.worlds) {
      export.exportLoadedChunksAsync(world)
    }
    return true
  }
}

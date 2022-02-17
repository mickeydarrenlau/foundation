package cloud.kubelet.foundation.heimdall.export

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin

class ExportChunksCommand(private val plugin: Plugin) : CommandExecutor {
  override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
    plugin.slF4JLogger.info("Exporting All Chunks")
    for (world in sender.server.worlds) {
      val export = ChunkExporter(plugin, sender.server, world)
      export.exportLoadedChunksAsync()
    }
    return true
  }
}

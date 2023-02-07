package gay.pizza.foundation.heimdall.plugin.load

import gay.pizza.foundation.heimdall.load.WorldLoadFormat
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin
import java.nio.file.Paths
import kotlin.io.path.exists
import kotlin.io.path.inputStream

class ImportWorldLoadCommand(private val plugin: Plugin) : CommandExecutor {
  override fun onCommand(
    sender: CommandSender,
    command: Command,
    label: String,
    args: Array<out String>
  ): Boolean {
    if (args.size != 1) {
      sender.sendMessage("Usage: import_world_load <path>")
      return true
    }
    val pathString = args[0]
    val path = Paths.get(pathString)
    if (!path.exists()) {
      sender.sendMessage("Path '${path}' not found.")
    }
    val format = Json.decodeFromStream(WorldLoadFormat.serializer(), path.inputStream())
    val reassembler = WorldReassembler(plugin, sender.server, format) { message ->
      sender.sendMessage(message)
    }
    reassembler.loadInBackground()
    return true
  }
}

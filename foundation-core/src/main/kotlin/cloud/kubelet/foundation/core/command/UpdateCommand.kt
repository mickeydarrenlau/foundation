package cloud.kubelet.foundation.core.command

import cloud.kubelet.foundation.core.update.UpdateUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class UpdateCommand : CommandExecutor {
  override fun onCommand(
    sender: CommandSender,
    command: Command,
    label: String,
    args: Array<out String>
  ): Boolean {
    // TODO: Move to separate thread?
    val modules = UpdateUtil.fetchManifest()
    val plugins = sender.server.pluginManager.plugins.associateBy { it.name.lowercase() }

    sender.sendMessage("Updates:")
    modules.forEach { (name, manifest) ->
      // Dumb naming problem. Don't want to fix it right now.
      val plugin = if (name == "foundation-core") {
        plugins["foundation"]
      } else {
        plugins[name.lowercase()]
      }

      if (plugin == null) {
        sender.sendMessage("Plugin in manifest, but not installed: $name (${manifest.version})")
      } else {
        sender.sendMessage("${plugin.name}: ${manifest.version} (have ${plugin.description.version})")
      }
    }

    return true
  }
}
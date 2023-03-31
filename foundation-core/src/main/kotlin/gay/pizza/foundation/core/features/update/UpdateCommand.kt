package gay.pizza.foundation.core.features.update

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin

class UpdateCommand(val plugin: Plugin) : CommandExecutor {
  override fun onCommand(
    sender: CommandSender,
    command: Command,
    label: String,
    args: Array<out String>
  ): Boolean {
    val shouldRestart = args.isNotEmpty() && args[0] == "restart"
    UpdateService.updatePlugins(plugin, sender, onFinish = { updated ->
      if (!updated) return@updatePlugins
      if (shouldRestart) {
        sender.server.shutdown()
      }
    })
    return true
  }
}

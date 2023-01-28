package gay.pizza.foundation.chaos

import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ChaosToggleCommand : CommandExecutor {
  override fun onCommand(
    sender: CommandSender,
    command: Command,
    label: String,
    args: Array<out String>
  ): Boolean {
    val plugin = sender.server.pluginManager.getPlugin("Foundation-Chaos") as FoundationChaosPlugin
    if (!plugin.config.allowed) {
      sender.sendMessage("Chaos is not allowed.")
      return true
    }
    val controller = plugin.controller
    if (controller.state.get()) {
      controller.unload()
      sender.server.broadcast(Component.text("Chaos Mode Disabled"))
    } else {
      controller.load()
      sender.server.broadcast(Component.text("Chaos Mode Enabled"))
    }
    return true
  }
}

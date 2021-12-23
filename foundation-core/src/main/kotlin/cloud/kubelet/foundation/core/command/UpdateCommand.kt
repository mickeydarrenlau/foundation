package cloud.kubelet.foundation.core.command

import cloud.kubelet.foundation.core.service.UpdateService
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
    UpdateService.updatePlugins(sender)
    return true
  }
}
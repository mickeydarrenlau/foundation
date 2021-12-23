package cloud.kubelet.foundation.core.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SpawnCommand : CommandExecutor {
  override fun onCommand(
    sender: CommandSender,
    command: Command,
    label: String,
    args: Array<out String>
  ): Boolean {
    if (sender !is Player) {
      sender.sendMessage("You are not a player.")
      return true
    }

    sender.teleport(sender.world.spawnLocation)

    return true
  }
}

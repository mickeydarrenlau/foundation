package cloud.kubelet.foundation.core.features.world

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetSpawnCommand : CommandExecutor {
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

    val loc = sender.location
    sender.world.setSpawnLocation(loc.blockX, loc.blockY, loc.blockZ)

    sender.sendMessage("World spawn point set.")

    return true
  }
}

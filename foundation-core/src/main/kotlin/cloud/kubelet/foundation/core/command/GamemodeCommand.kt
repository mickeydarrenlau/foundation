package cloud.kubelet.foundation.core.command

import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GamemodeCommand(private val gameMode: GameMode) : CommandExecutor {
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

    sender.gameMode = gameMode
    sender.sendMessage("Switched gamemode to ${gameMode.name.lowercase()}")

    return true
  }
}

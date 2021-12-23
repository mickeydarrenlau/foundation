package cloud.kubelet.foundation.core.command

import cloud.kubelet.foundation.core.SortOrder
import cloud.kubelet.foundation.core.allPlayerStatisticsOf
import org.bukkit.Statistic
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class LeaderboardCommand : CommandExecutor, TabCompleter {
  private val leaderboards = listOf(
    LeaderboardType("player-kills", Statistic.PLAYER_KILLS, "Player Kills", "kills"),
    LeaderboardType("mob-kills", Statistic.MOB_KILLS, "Mob Kills", "kills"),
    LeaderboardType("animals-bred", Statistic.ANIMALS_BRED, "Animals Bred", "animals"),
    LeaderboardType("chest-opens", Statistic.CHEST_OPENED, "Chest Opens", "opens")
  )

  override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
    if (args.size != 1) {
      sender.sendMessage("Leaderboard type not specified.")
      return true
    }

    val leaderboardType = leaderboards.firstOrNull { it.id == args[0] }
    if (leaderboardType == null) {
      sender.sendMessage("Leaderboard type is unknown.")
      return true
    }
    val statistics = sender.server.allPlayerStatisticsOf(leaderboardType.statistic, order = SortOrder.Descending)
    val topFivePlayers = statistics.take(5)
    sender.sendMessage(
      "${leaderboardType.friendlyName} Leaderboard:",
      *topFivePlayers.map { "* ${it.first.name}: ${it.second} ${leaderboardType.unit}" }.toTypedArray()
    )
    return true
  }

  class LeaderboardType(val id: String, val statistic: Statistic, val friendlyName: String, val unit: String)

  override fun onTabComplete(
    sender: CommandSender,
    command: Command,
    alias: String,
    args: Array<out String>
  ): MutableList<String> = when {
    args.isEmpty() -> {
      leaderboards.map { it.id }.toMutableList()
    }
    args.size == 1 -> {
      leaderboards.map { it.id }.filter { it.startsWith(args[0]) }.toMutableList()
    }
    else -> {
      mutableListOf()
    }
  }
}

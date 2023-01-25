package gay.pizza.foundation.core.features.player

import org.bukkit.WeatherType
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class LocalWeatherCommand : CommandExecutor, TabCompleter {
  private val weatherTypes = WeatherType.values().associateBy { it.name.lowercase() }

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
    if (args.size != 1) {
      return false
    }

    val name = args[0].lowercase()
    val weatherType = weatherTypes[name]
    if (weatherType == null) {
      sender.sendMessage("Not a valid weather type.")
      return true
    }

    sender.setPlayerWeather(weatherType)
    sender.sendMessage("Weather set to \"$name\"")

    return true
  }

  override fun onTabComplete(
    sender: CommandSender,
    command: Command,
    alias: String,
    args: Array<out String>
  ): List<String> = when {
    args.isEmpty() -> weatherTypes.keys.toList()
    args.size == 1 -> weatherTypes.filterKeys { it.startsWith(args[0]) }.keys.toList()
    else -> listOf()
  }
}

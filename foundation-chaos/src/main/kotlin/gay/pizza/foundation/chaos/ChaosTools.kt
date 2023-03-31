package gay.pizza.foundation.chaos

import org.bukkit.Location
import org.bukkit.Server
import org.bukkit.entity.Player

fun Location.nearestPlayer(): Player? =
  world?.players?.minByOrNull { it.location.distance(this) }

fun Server.randomPlayer(): Player? =
  onlinePlayers.randomOrNull()

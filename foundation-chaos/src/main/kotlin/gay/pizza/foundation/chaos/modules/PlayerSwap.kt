package gay.pizza.foundation.chaos.modules

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class PlayerSwap(val plugin: Plugin) : ChaosModule {
  override fun id(): String = "player-swap"
  override fun name(): String = "Player Swap"
  override fun what(): String = "Randomly swaps player positions."

  override fun activate() {
    for (world in plugin.server.worlds) {
      if (world.playerCount <= 0) {
        continue
      }

      val players = world.players
      val map = mutableMapOf<Player, Location>()
      for (player in players) {
        val next = players.filter { it != player }.randomOrNull() ?: continue
        map[player] = next.location.clone()
      }

      for ((player, next) in map) {
        player.teleport(next)
      }
    }
  }
}

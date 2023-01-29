package gay.pizza.foundation.chaos.modules

import org.bukkit.plugin.Plugin

class KillRandomPlayer(val plugin: Plugin) : ChaosModule {
  override fun id(): String = "kill-random-player"
  override fun name(): String = "Random Kill"
  override fun what(): String = "Kill a random player."

  override fun activate() {
    val player = plugin.server.onlinePlayers.randomOrNull() ?: return
    player.damage(1000000.0)
  }
}

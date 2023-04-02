package gay.pizza.foundation.chaos.modules

import org.bukkit.plugin.Plugin

object ChaosModules {
  fun all(plugin: Plugin) = listOf(
    NearestPlayerEntitySpawn(plugin),
    TeleportAllEntitiesNearestPlayer(plugin),
    KillRandomPlayer(plugin),
    TntAllPlayers(plugin),
    MegaTnt(plugin),
    PlayerSwap(plugin),
    WorldSwapper(plugin),
    ChunkEnterRotate()
  ).shuffled()
}

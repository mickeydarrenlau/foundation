package gay.pizza.foundation.chaos.modules

import gay.pizza.foundation.chaos.nearestPlayer
import org.bukkit.plugin.Plugin

class TeleportAllEntitiesNearestPlayer(val plugin: Plugin) : ChaosModule {
  override fun id(): String = "teleport-all-entities-nearest-player"
  override fun name(): String = "Monster Me Once"
  override fun what(): String = "Teleport all entities to the nearest player"

  override fun activate() {
    for (world in plugin.server.worlds) {
      for (entity in world.entities) {
        val player = entity.location.nearestPlayer()
        if (player != null) {
          entity.teleport(player)
        }
      }
    }
  }
}

package cloud.kubelet.foundation.core.features.endergrief

import cloud.kubelet.foundation.core.abstraction.Feature
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityChangeBlockEvent

class EndergriefFeature : Feature() {
  override fun enable() {
  }

  override fun disable() {}

  @EventHandler(priority = EventPriority.HIGHEST)
  fun onEntityChangeBlock(event: EntityChangeBlockEvent) {
    if (event.entity.type == EntityType.ENDERMAN) {
      event.isCancelled = true
    }
  }
}

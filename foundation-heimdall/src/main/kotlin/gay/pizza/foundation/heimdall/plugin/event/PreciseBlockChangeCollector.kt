package gay.pizza.foundation.heimdall.plugin.event

import gay.pizza.foundation.heimdall.plugin.buffer.IEventBuffer
import gay.pizza.foundation.heimdall.plugin.model.HeimdallConfig
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockPhysicsEvent

class PreciseBlockChangeCollector(val config: HeimdallConfig, val buffer: IEventBuffer) : EventCollector<BlockChange> {
  private var changes = mutableMapOf<String, BlockChange>()

  override fun beforeBufferFlush() {
    if (config.blockChangePreciseImmediate) {
      return
    }
    val changesToInsert = changes
    changes = mutableMapOf()
    buffer.pushAll(changesToInsert.values.toList())
  }

  @EventHandler(priority = EventPriority.MONITOR)
  fun onBlockPhysics(event: BlockPhysicsEvent) {
    val change = BlockChangeConversions.physics(event)
    if (config.blockChangePreciseImmediate) {
      buffer.push(change)
      return
    }
    changes[(event.sourceBlock.location.world.name to listOf(
      event.sourceBlock.location.x.toLong(),
      event.sourceBlock.location.y.toLong(),
      event.sourceBlock.location.z.toLong()
    )).toString()] = change
  }
}

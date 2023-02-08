package gay.pizza.foundation.heimdall.plugin.event

import gay.pizza.foundation.heimdall.plugin.buffer.EventBuffer
import gay.pizza.foundation.heimdall.plugin.buffer.IEventBuffer
import gay.pizza.foundation.heimdall.table.BlockPlaceTable
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockPlaceEvent
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insert
import java.time.Instant
import java.util.*

class BlockPlace(
  val playerUniqueIdentity: UUID,
  val location: Location,
  val material: Material,
  val blockData: String? = null,
  val timestamp: Instant = Instant.now()
) : HeimdallEvent() {
  constructor(event: BlockPlaceEvent) : this(
    event.player.uniqueId,
    event.block.location,
    event.block.type,
    event.block.blockData.asString
  )

  override fun store(transaction: Transaction) {
    transaction.apply {
      BlockPlaceTable.insert {
        putPlayerTimedLocalEvent(it, timestamp, location, playerUniqueIdentity)
        it[block] = material.key.toString()
        it[blockData] = this@BlockPlace.blockData
      }
    }
  }

  class Collector(val buffer: IEventBuffer) : EventCollector<BlockPlace> {
    @EventHandler
    fun onBlockPlaced(event: BlockPlaceEvent) = buffer.push(BlockPlace(event))
  }

  companion object : EventCollectorProvider<BlockPlace> {
    override fun collector(buffer: EventBuffer): EventCollector<BlockPlace> = Collector(buffer)
  }
}

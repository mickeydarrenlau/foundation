package gay.pizza.foundation.heimdall.plugin.event

import gay.pizza.foundation.heimdall.plugin.buffer.EventBuffer
import gay.pizza.foundation.heimdall.plugin.buffer.IEventBuffer
import gay.pizza.foundation.heimdall.table.BlockBreakTable
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insert
import java.time.Instant
import java.util.*

class BlockBreak(
  val playerUniqueIdentity: UUID,
  val location: Location,
  val material: Material,
  val blockData: String? = null,
  val timestamp: Instant = Instant.now()
) : HeimdallEvent() {
  constructor(event: BlockBreakEvent) : this(
    event.player.uniqueId,
    event.block.location,
    event.block.type,
    event.block.blockData.asString
  )

  override fun store(transaction: Transaction) {
    transaction.apply {
      BlockBreakTable.insert {
        putPlayerTimedLocalEvent(it, timestamp, location, playerUniqueIdentity)
        it[block] = material.key.toString()
        it[blockData] = this@BlockBreak.blockData
      }
    }
  }

  class Collector(val buffer: IEventBuffer) : EventCollector<BlockBreak> {
    @EventHandler
    fun onBlockBroken(event: BlockBreakEvent) = buffer.push(BlockBreak(event))
  }

  companion object : EventCollectorProvider<BlockBreak> {
    override fun collector(buffer: EventBuffer): EventCollector<BlockBreak> = Collector(buffer)
  }
}

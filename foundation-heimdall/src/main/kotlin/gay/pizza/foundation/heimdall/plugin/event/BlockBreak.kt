package gay.pizza.foundation.heimdall.plugin.event

import gay.pizza.foundation.heimdall.table.BlockBreakTable
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.block.BlockBreakEvent
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insert
import java.time.Instant
import java.util.*

class BlockBreak(
  val playerUniqueIdentity: UUID,
  val location: Location,
  val material: Material,
  val timestamp: Instant = Instant.now()
) : HeimdallEvent() {
  constructor(event: BlockBreakEvent) : this(
    event.player.uniqueId,
    event.block.location,
    event.block.type
  )

  override fun store(transaction: Transaction) {
    transaction.apply {
      BlockBreakTable.insert {
        putPlayerTimedLocalEvent(it, timestamp, location, playerUniqueIdentity)
        it[block] = material.key.toString()
      }
    }
  }
}

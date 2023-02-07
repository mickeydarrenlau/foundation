package gay.pizza.foundation.heimdall.plugin.event

import gay.pizza.foundation.heimdall.table.BlockPlaceTable
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.block.BlockPlaceEvent
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insert
import java.time.Instant
import java.util.*

class BlockPlace(
  val playerUniqueIdentity: UUID,
  val location: Location,
  val material: Material,
  val timestamp: Instant = Instant.now()
) : HeimdallEvent() {
  constructor(event: BlockPlaceEvent) : this(
    event.player.uniqueId,
    event.block.location,
    event.block.type
  )

  override fun store(transaction: Transaction) {
    transaction.apply {
      BlockPlaceTable.insert {
        putPlayerTimedLocalEvent(it, timestamp, location, playerUniqueIdentity)
        it[block] = material.key.toString()
      }
    }
  }
}

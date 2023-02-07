package gay.pizza.foundation.heimdall.plugin.event

import gay.pizza.foundation.heimdall.table.PlayerPositionTable
import org.bukkit.Location
import org.bukkit.event.player.PlayerMoveEvent
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insert
import java.time.Instant
import java.util.*

class PlayerPosition(
  val playerUniqueIdentity: UUID,
  val location: Location,
  val timestamp: Instant = Instant.now()
) : HeimdallEvent() {
  constructor(event: PlayerMoveEvent) : this(
    event.player.uniqueId,
    event.to
  )

  override fun store(transaction: Transaction) {
    transaction.apply {
      PlayerPositionTable.insert {
        putPlayerTimedLocalEvent(it, timestamp, location, playerUniqueIdentity)
      }
    }
  }
}

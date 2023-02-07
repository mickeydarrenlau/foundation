package gay.pizza.foundation.heimdall.plugin.event

import gay.pizza.foundation.heimdall.table.PlayerAdvancementTable
import org.bukkit.Location
import org.bukkit.advancement.Advancement
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insert
import java.time.Instant
import java.util.*

class PlayerAdvancement(
  val playerUniqueIdentity: UUID,
  val location: Location,
  val advancement: Advancement,
  val timestamp: Instant = Instant.now()
) : HeimdallEvent() {
  constructor(event: PlayerAdvancementDoneEvent) : this(
    event.player.uniqueId,
    event.player.location,
    event.advancement
  )

  override fun store(transaction: Transaction) {
    transaction.apply {
      PlayerAdvancementTable.insert {
        putPlayerTimedLocalEvent(it, timestamp, location, playerUniqueIdentity)
        it[advancement] = this@PlayerAdvancement.advancement.key.toString()
      }
    }
  }
}

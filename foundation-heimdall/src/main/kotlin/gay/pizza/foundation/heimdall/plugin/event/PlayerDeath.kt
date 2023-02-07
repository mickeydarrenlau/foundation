package gay.pizza.foundation.heimdall.plugin.event

import gay.pizza.foundation.heimdall.table.PlayerDeathTable
import org.bukkit.Location
import org.bukkit.event.entity.PlayerDeathEvent
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insert
import java.time.Instant
import java.util.*

class PlayerDeath(
  val playerUniqueIdentity: UUID,
  val location: Location,
  val experienceLevel: Float,
  val deathMessage: String?,
  val timestamp: Instant = Instant.now()
) : HeimdallEvent() {
  constructor(event: PlayerDeathEvent, deathMessage: String? = null) : this(
    event.player.uniqueId,
    event.player.location,
    event.player.exp,
    deathMessage
  )

  override fun store(transaction: Transaction) {
    transaction.apply {
      PlayerDeathTable.insert {
        putPlayerTimedLocalEvent(it, timestamp, location, playerUniqueIdentity)
        it[experience] = experienceLevel.toDouble()
        it[message] = deathMessage
      }
    }
  }
}

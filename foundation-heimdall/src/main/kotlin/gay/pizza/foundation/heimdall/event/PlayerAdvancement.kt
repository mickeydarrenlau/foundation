package gay.pizza.foundation.heimdall.event

import gay.pizza.foundation.heimdall.table.PlayerAdvancementTable
import org.bukkit.Location
import org.bukkit.advancement.Advancement
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insert
import java.time.Instant
import java.util.UUID

class PlayerAdvancement(
  val playerUniqueIdentity: UUID,
  val location: Location,
  val advancement: Advancement,
  val timestamp: Instant = Instant.now()
) : HeimdallEvent() {
  constructor(event: PlayerAdvancementDoneEvent) : this(event.player.uniqueId, event.player.location, event.advancement)

  override fun store(transaction: Transaction) {
    transaction.apply {
      PlayerAdvancementTable.insert {
        it[time] = timestamp
        it[player] = playerUniqueIdentity
        it[world] = location.world.uid
        it[x] = location.x
        it[y] = location.y
        it[z] = location.z
        it[pitch] = location.pitch.toDouble()
        it[yaw] = location.yaw.toDouble()
        it[advancement] = this@PlayerAdvancement.advancement.key.toString()
      }
    }
  }
}

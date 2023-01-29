package gay.pizza.foundation.heimdall.plugin.event

import gay.pizza.foundation.heimdall.table.EntityKillTable
import org.bukkit.Location
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insert
import java.time.Instant
import java.util.*

class EntityKill(
  val playerUniqueIdentity: UUID,
  val location: Location,
  val entityUniqueIdentity: UUID,
  val entityTypeName: String,
  val timestamp: Instant = Instant.now()
) : HeimdallEvent() {
  override fun store(transaction: Transaction) {
    transaction.apply {
      EntityKillTable.insert {
        it[time] = timestamp
        it[player] = playerUniqueIdentity
        it[world] = location.world.uid
        it[x] = location.x
        it[y] = location.y
        it[z] = location.z
        it[pitch] = location.pitch.toDouble()
        it[yaw] = location.yaw.toDouble()
        it[entity] = entityUniqueIdentity
        it[entityType] = entityTypeName
      }
    }
  }
}

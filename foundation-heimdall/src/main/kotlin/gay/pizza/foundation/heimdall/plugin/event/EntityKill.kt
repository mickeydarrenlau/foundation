package gay.pizza.foundation.heimdall.plugin.event

import gay.pizza.foundation.heimdall.plugin.buffer.EventBuffer
import gay.pizza.foundation.heimdall.plugin.buffer.IEventBuffer
import gay.pizza.foundation.heimdall.table.EntityKillTable
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDeathEvent
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
        putPlayerTimedLocalEvent(it, timestamp, location, playerUniqueIdentity)
        it[entity] = entityUniqueIdentity
        it[entityType] = entityTypeName
      }
    }
  }

  class Collector(val buffer: IEventBuffer) : EventCollector<EntityKill> {
    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
      val killer = event.entity.killer ?: return
      buffer.push(
        EntityKill(
          killer.uniqueId,
          killer.location,
          event.entity.uniqueId,
          event.entityType.key.toString()
        )
      )
    }
  }

  companion object : EventCollectorProvider<EntityKill> {
    override fun collector(buffer: EventBuffer): EventCollector<EntityKill> = Collector(buffer)
  }
}

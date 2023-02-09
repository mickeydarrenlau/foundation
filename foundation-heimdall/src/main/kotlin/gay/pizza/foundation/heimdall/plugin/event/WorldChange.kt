package gay.pizza.foundation.heimdall.plugin.event

import gay.pizza.foundation.heimdall.plugin.buffer.EventBuffer
import gay.pizza.foundation.heimdall.plugin.buffer.IEventBuffer
import gay.pizza.foundation.heimdall.plugin.model.HeimdallConfig
import gay.pizza.foundation.heimdall.table.WorldChangeTable
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insert
import java.time.Instant
import java.util.*

class WorldChange(
  val playerUniqueIdentity: UUID,
  val fromWorldId: UUID,
  val fromWorldActualName: String,
  val toWorldId: UUID,
  val toWorldActualName: String,
  val timestamp: Instant = Instant.now()
) : HeimdallEvent() {
  override fun store(transaction: Transaction, index: Int) {
    transaction.apply {
      WorldChangeTable.insert {
        putTimedEvent(it, timestamp)
        it[player] = playerUniqueIdentity
        it[fromWorld] = fromWorldId
        it[fromWorldName] = fromWorldActualName
        it[toWorld] = toWorldId
        it[toWorldName] = toWorldActualName
      }
    }
  }

  class Collector(val buffer: IEventBuffer) : EventCollector<WorldChange> {
    @EventHandler
    fun onWorldLoad(event: PlayerChangedWorldEvent) = buffer.push(
      WorldChange(
        event.player.uniqueId,
        event.from.uid,
        event.from.name,
        event.player.world.uid,
        event.player.world.name
      )
    )
  }

  companion object : EventCollectorProvider<WorldChange> {
    override fun collector(config: HeimdallConfig, buffer: EventBuffer): EventCollector<WorldChange> = Collector(buffer)
  }
}

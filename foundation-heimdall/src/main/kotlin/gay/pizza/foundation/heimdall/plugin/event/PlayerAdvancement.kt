package gay.pizza.foundation.heimdall.plugin.event

import gay.pizza.foundation.heimdall.plugin.buffer.EventBuffer
import gay.pizza.foundation.heimdall.plugin.buffer.IEventBuffer
import gay.pizza.foundation.heimdall.plugin.model.HeimdallConfig
import gay.pizza.foundation.heimdall.table.PlayerAdvancementTable
import org.bukkit.Location
import org.bukkit.advancement.Advancement
import org.bukkit.event.EventHandler
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
    event.player.location.clone(),
    event.advancement
  )

  override fun store(transaction: Transaction, index: Int) {
    transaction.apply {
      PlayerAdvancementTable.insert {
        putPlayerTimedLocalEvent(it, timestamp, location, playerUniqueIdentity)
        it[advancement] = this@PlayerAdvancement.advancement.key.toString()
      }
    }
  }

  class Collector(val buffer: IEventBuffer) : EventCollector<PlayerAdvancement> {
    @EventHandler
    fun onPlayerAdvancementDone(event: PlayerAdvancementDoneEvent) = buffer.push(PlayerAdvancement(event))
  }

  companion object : EventCollectorProvider<PlayerAdvancement> {
    override fun collector(config: HeimdallConfig, buffer: EventBuffer): EventCollector<PlayerAdvancement> = Collector(buffer)
  }
}

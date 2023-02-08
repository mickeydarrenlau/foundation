package gay.pizza.foundation.heimdall.plugin.event

import gay.pizza.foundation.heimdall.plugin.buffer.EventBuffer
import gay.pizza.foundation.heimdall.plugin.buffer.IEventBuffer
import gay.pizza.foundation.heimdall.table.PlayerSessionTable
import org.bukkit.Server
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insert
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class PlayerSession(
  val playerUniqueIdentity: UUID,
  val playerName: String,
  val startTimeInstant: Instant,
  val endTimeInstant: Instant
) : HeimdallEvent() {
  override fun store(transaction: Transaction) {
    transaction.apply {
      PlayerSessionTable.insert {
        it[id] = UUID.randomUUID()
        it[player] = playerUniqueIdentity
        it[name] = playerName
        it[startTime] = startTimeInstant
        it[endTime] = endTimeInstant
      }
    }
  }

  class Collector(val buffer: IEventBuffer) : EventCollector<PlayerSession> {
    private val playerJoinTimes = ConcurrentHashMap<UUID, Instant>()

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
      playerJoinTimes[event.player.uniqueId] = Instant.now()
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
      val startTime = playerJoinTimes.remove(event.player.uniqueId) ?: return
      val endTime = Instant.now()
      buffer.push(PlayerSession(event.player.uniqueId, event.player.name, startTime, endTime))
    }

    override fun onPluginDisable(server: Server) {
      val endTime = Instant.now()
      for (playerId in playerJoinTimes.keys().toList()) {
        val startTime = playerJoinTimes.remove(playerId) ?: continue
        buffer.push(PlayerSession(
          playerId,
          server.getPlayer(playerId)?.name ?: "__unknown__",
          startTime,
          endTime
        ))
      }
    }
  }

  companion object : EventCollectorProvider<PlayerSession> {
    override fun collector(buffer: EventBuffer): EventCollector<PlayerSession> = Collector(buffer)
  }
}

package gay.pizza.foundation.heimdall.event

import gay.pizza.foundation.heimdall.table.PlayerSessionTable
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insert
import java.time.Instant
import java.util.UUID

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
}

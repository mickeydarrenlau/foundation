package cloud.kubelet.foundation.heimdall.event

import cloud.kubelet.foundation.heimdall.table.PlayerSessionTable
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insert
import java.time.Instant
import java.util.*

class PlayerSession(
  val playerUniqueIdentity: UUID,
  val playerName: String,
  val startTimeInstant: Instant,
  val endTimeInstant: Instant
) : HeimdallEvent() {
  override fun store(transaction: Transaction) {
    transaction.apply {
      PlayerSessionTable.insert {
        it[player] = playerUniqueIdentity
        it[name] = playerName
        it[startTime] = startTimeInstant
        it[endTime] = endTimeInstant
      }
    }
  }
}

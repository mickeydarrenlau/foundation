package cloud.kubelet.foundation.heimdall.event

import cloud.kubelet.foundation.heimdall.table.WorldChangeTable
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
  override fun store(transaction: Transaction) {
    transaction.apply {
      WorldChangeTable.insert {
        it[time] = timestamp
        it[player] = playerUniqueIdentity
        it[fromWorld] = fromWorldId
        it[fromWorldName] = fromWorldActualName
        it[toWorld] = toWorldId
        it[toWorldName] = toWorldActualName
      }
    }
  }
}

package cloud.kubelet.foundation.heimdall.event

import cloud.kubelet.foundation.heimdall.table.PlayerPositionTable
import org.bukkit.Location
import org.bukkit.event.player.PlayerMoveEvent
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insert
import java.time.Instant
import java.util.*

class PlayerPosition(
  val playerUniqueIdentity: UUID,
  val location: Location
) : HeimdallEvent() {
  constructor(event: PlayerMoveEvent) : this(event.player.uniqueId, event.to)

  override fun store(transaction: Transaction) {
    transaction.apply {
      PlayerPositionTable.insert {
        it[time] = Instant.now()
        it[player] = playerUniqueIdentity
        it[world] = location.world.uid
        it[x] = location.x
        it[y] = location.y
        it[z] = location.z
        it[pitch] = location.pitch.toDouble()
        it[yaw] = location.yaw.toDouble()
      }
    }
  }
}

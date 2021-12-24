package cloud.kubelet.foundation.heimdall.event

import cloud.kubelet.foundation.heimdall.storageBlockName
import cloud.kubelet.foundation.heimdall.table.BlockBreakTable
import cloud.kubelet.foundation.heimdall.table.PlayerPositionTable
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insert
import java.time.Instant
import java.util.*

class BlockBreak(
  val playerUniqueIdentity: UUID,
  val location: Location,
  val material: Material,
  val timestamp: Instant = Instant.now()
) : HeimdallEvent() {
  constructor(event: BlockBreakEvent) : this(event.player.uniqueId, event.block.location, event.block.type)

  override fun store(transaction: Transaction) {
    transaction.apply {
      BlockBreakTable.insert {
        it[time] = timestamp
        it[player] = playerUniqueIdentity
        it[world] = location.world.uid
        it[block] = material.storageBlockName
        it[x] = location.x
        it[y] = location.y
        it[z] = location.z
      }
    }
  }
}

package cloud.kubelet.foundation.heimdall.event

import cloud.kubelet.foundation.heimdall.storageBlockName
import cloud.kubelet.foundation.heimdall.table.BlockBreakTable
import cloud.kubelet.foundation.heimdall.table.BlockPlaceTable
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.block.BlockPlaceEvent
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insert
import java.time.Instant
import java.util.*

class BlockPlace(
  val playerUniqueIdentity: UUID,
  val location: Location,
  val material: Material,
  val timestamp: Instant = Instant.now()
) : HeimdallEvent() {
  constructor(event: BlockPlaceEvent) : this(event.player.uniqueId, event.block.location, event.block.type)

  override fun store(transaction: Transaction) {
    transaction.apply {
      BlockPlaceTable.insert {
        it[BlockBreakTable.time] = timestamp
        it[BlockBreakTable.player] = playerUniqueIdentity
        it[BlockBreakTable.world] = location.world.uid
        it[BlockBreakTable.x] = location.x
        it[BlockBreakTable.y] = location.y
        it[BlockBreakTable.z] = location.z
        it[BlockBreakTable.pitch] = location.pitch.toDouble()
        it[BlockBreakTable.yaw] = location.yaw.toDouble()
        it[BlockBreakTable.block] = material.storageBlockName
      }
    }
  }
}

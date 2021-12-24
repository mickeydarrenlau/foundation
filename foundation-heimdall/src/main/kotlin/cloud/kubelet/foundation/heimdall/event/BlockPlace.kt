package cloud.kubelet.foundation.heimdall.event

import cloud.kubelet.foundation.heimdall.storageBlockName
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
  val material: Material
) : HeimdallEvent() {
  constructor(event: BlockPlaceEvent) : this(event.player.uniqueId, event.block.location, event.block.type)

  override fun store(transaction: Transaction) {
    transaction.apply {
      BlockPlaceTable.insert {
        it[time] = Instant.now()
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

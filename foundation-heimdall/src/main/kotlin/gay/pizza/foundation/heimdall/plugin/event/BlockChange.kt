package gay.pizza.foundation.heimdall.plugin.event

import gay.pizza.foundation.heimdall.plugin.buffer.EventBuffer
import gay.pizza.foundation.heimdall.plugin.model.HeimdallConfig
import gay.pizza.foundation.heimdall.table.BlockChangeTable
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockState
import org.bukkit.block.data.BlockData
import org.bukkit.event.block.BlockEvent
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insert
import java.time.Instant
import java.util.*

class BlockChange(
  val playerUniqueIdentity: UUID? = null,
  val cause: String = "place",
  val location: Location,
  val material: Material,
  val blockData: String,
  val timestamp: Instant = Instant.now()
) : HeimdallEvent() {
  constructor(
    playerUniqueIdentity: UUID? = null,
    isBreak: Boolean = false,
    cause: String,
    event: BlockEvent,
    block: Block = event.block,
    state: BlockState = block.state,
    data: BlockData = state.blockData
  ) : this(
    playerUniqueIdentity = playerUniqueIdentity,
    cause = cause,
    location = block.location.clone(),
    material = if (isBreak) Material.AIR else data.material,
    blockData =
      if (isBreak)
        Material.AIR.createBlockData().asString
      else
        data.asString
  )

  override fun store(transaction: Transaction, index: Int) {
    transaction.apply {
      BlockChangeTable.insert {
        it[inc] = index
        putPlayerTimedLocalEvent(it, timestamp, location, playerUniqueIdentity)
        it[block] = material.key.toString()
        it[data] = this@BlockChange.blockData
        it[cause] = this@BlockChange.cause
      }
    }
  }

  companion object : EventCollectorProvider<BlockChange> {
    override fun collector(config: HeimdallConfig, buffer: EventBuffer): EventCollector<BlockChange> =
      if (config.blockChangePrecise) {
        PreciseBlockChangeCollector(config, buffer)
      } else {
        AccurateBlockChangeCollector(buffer)
      }
  }
}

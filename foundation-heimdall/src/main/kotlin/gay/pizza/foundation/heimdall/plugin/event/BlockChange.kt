package gay.pizza.foundation.heimdall.plugin.event

import gay.pizza.foundation.heimdall.plugin.buffer.EventBuffer
import gay.pizza.foundation.heimdall.plugin.buffer.IEventBuffer
import gay.pizza.foundation.heimdall.table.BlockChangeTable
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockBurnEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.block.BlockDispenseEvent
import org.bukkit.event.block.BlockEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockFadeEvent
import org.bukkit.event.block.BlockFormEvent
import org.bukkit.event.block.BlockGrowEvent
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.BlockSpreadEvent
import org.bukkit.event.block.FluidLevelChangeEvent
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
    block: Block = event.block
  ) : this(
    playerUniqueIdentity = playerUniqueIdentity,
    cause = cause,
    location = block.location,
    material = if (isBreak) Material.AIR else block.type,
    blockData = if (isBreak) Material.AIR.createBlockData().asString
      else block.blockData.asString
  )

  override fun store(transaction: Transaction) {
    transaction.apply {
      BlockChangeTable.insert {
        putPlayerTimedLocalEvent(it, timestamp, location, playerUniqueIdentity)
        it[block] = material.key.toString()
        it[data] = this@BlockChange.blockData
        it[cause] = this@BlockChange.cause
      }
    }
  }

  class Collector(val buffer: IEventBuffer) : EventCollector<BlockChange> {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockPlaced(event: BlockPlaceEvent) = buffer.push(
      BlockChange(
        playerUniqueIdentity = event.player.uniqueId,
        event = event,
        cause = "place",
        isBreak = false
      )
    )

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockBreak(event: BlockBreakEvent) = buffer.push(
      BlockChange(
        playerUniqueIdentity = event.player.uniqueId,
        event = event,
        cause = "break",
        isBreak = true
      )
    )

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockExplode(event: BlockExplodeEvent) = event.blockList().forEach { block ->
      buffer.push(
        BlockChange(
          event = event,
          cause = "explode",
          isBreak = true,
          block = block
        )
      )
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockBurn(event: BlockBurnEvent) = buffer.push(
      BlockChange(
        playerUniqueIdentity = null,
        event = event,
        cause = "burn",
        isBreak = true
      )
    )

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockDamage(event: BlockDamageEvent) = buffer.push(
      BlockChange(
        playerUniqueIdentity = null,
        event = event,
        cause = "damage"
      )
    )

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockForm(event: BlockFormEvent) = buffer.push(
      BlockChange(
        playerUniqueIdentity = null,
        event = event,
        cause = "form"
      )
    )

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockGrow(event: BlockGrowEvent) = buffer.push(
      BlockChange(
        playerUniqueIdentity = null,
        event = event,
        cause = "grow"
      )
    )

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockFade(event: BlockFadeEvent) = buffer.push(
      BlockChange(
        playerUniqueIdentity = null,
        event = event,
        cause = "fade"
      )
    )

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockIgnite(event: BlockIgniteEvent) = buffer.push(
      BlockChange(
        playerUniqueIdentity = null,
        event = event,
        cause = "ignite"
      )
    )

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockDispense(event: BlockDispenseEvent) = buffer.push(
      BlockChange(
        playerUniqueIdentity = null,
        event = event,
        cause = "dispense"
      )
    )

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockSpread(event: BlockSpreadEvent) = buffer.push(
      BlockChange(
        playerUniqueIdentity = null,
        event = event,
        cause = "spread"
      )
    )

    @EventHandler(priority = EventPriority.MONITOR)
    fun onFluidLevelChange(event: FluidLevelChangeEvent) = buffer.push(
      BlockChange(
        playerUniqueIdentity = null,
        event = event,
        cause = "fluid-level-change"
      )
    )
  }

  companion object : EventCollectorProvider<BlockChange> {
    override fun collector(buffer: EventBuffer): EventCollector<BlockChange> = Collector(buffer)
  }
}

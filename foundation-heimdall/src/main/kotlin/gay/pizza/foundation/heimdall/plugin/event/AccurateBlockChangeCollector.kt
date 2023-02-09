package gay.pizza.foundation.heimdall.plugin.event

import gay.pizza.foundation.heimdall.plugin.buffer.IEventBuffer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.*

class AccurateBlockChangeCollector(val buffer: IEventBuffer) : EventCollector<BlockChange> {
  @EventHandler(priority = EventPriority.MONITOR)
  fun onBlockPlaced(event: BlockPlaceEvent) =
    buffer.push(BlockChangeConversions.blockPlace(event))

  @EventHandler(priority = EventPriority.MONITOR)
  fun onBlockBreak(event: BlockBreakEvent) =
    buffer.push(BlockChangeConversions.blockBreak(event))

  @EventHandler(priority = EventPriority.MONITOR)
  fun onBlockExplode(event: BlockExplodeEvent) =
    buffer.pushAll(BlockChangeConversions.blockExplode(event))

  @EventHandler(priority = EventPriority.MONITOR)
  fun onBlockBurn(event: BlockBurnEvent) =
    buffer.push(BlockChangeConversions.blockBurn(event))

  @EventHandler(priority = EventPriority.MONITOR)
  fun onBlockDamage(event: BlockDamageEvent) =
    buffer.push(BlockChangeConversions.blockDamage(event))

  @EventHandler(priority = EventPriority.MONITOR)
  fun onBlockForm(event: BlockFormEvent) =
    buffer.push(BlockChangeConversions.blockForm(event))

  @EventHandler(priority = EventPriority.MONITOR)
  fun onBlockGrow(event: BlockGrowEvent) =
    buffer.push(BlockChangeConversions.blockGrow(event))

  @EventHandler(priority = EventPriority.MONITOR)
  fun onBlockFade(event: BlockFadeEvent) =
    buffer.push(BlockChangeConversions.blockFade(event))

  @EventHandler(priority = EventPriority.MONITOR)
  fun onBlockIgnite(event: BlockIgniteEvent) =
    buffer.push(BlockChangeConversions.blockIgnite(event))

  @EventHandler(priority = EventPriority.MONITOR)
  fun onBlockSpread(event: BlockSpreadEvent) =
    buffer.push(BlockChangeConversions.blockSpread(event))

  @EventHandler(priority = EventPriority.MONITOR)
  fun onFluidLevelChange(event: FluidLevelChangeEvent) =
    buffer.push(BlockChangeConversions.fluidLevelChange(event))

  @EventHandler(priority = EventPriority.MONITOR)
  fun onSpongeAbsorb(event: SpongeAbsorbEvent) =
    buffer.pushAll(BlockChangeConversions.spongeAbsorb(event))

  @EventHandler(priority = EventPriority.MONITOR)
  fun onSignChange(event: SignChangeEvent) =
    buffer.push(BlockChangeConversions.signChange(event))

  @EventHandler(priority = EventPriority.MONITOR)
  fun onMoistureChange(event: MoistureChangeEvent) =
    buffer.push(BlockChangeConversions.moistureChange(event))

  @EventHandler(priority = EventPriority.MONITOR)
  fun onBlockCook(event: BlockCookEvent) =
    buffer.push(BlockChangeConversions.blockCook(event))
}

package gay.pizza.foundation.heimdall.plugin.event

import org.bukkit.event.block.*

object BlockChangeConversions {
  fun blockPlace(event: BlockPlaceEvent): BlockChange =
    BlockChange(
      playerUniqueIdentity = event.player.uniqueId,
      isBreak = false,
      cause = "place",
      event = event
    )

  fun blockBreak(event: BlockBreakEvent): BlockChange =
    BlockChange(
      playerUniqueIdentity = event.player.uniqueId,
      isBreak = true,
      cause = "break",
      event = event
    )

  fun blockExplode(event: BlockExplodeEvent): List<BlockChange> =
    mutableListOf<BlockChange>().apply {
      addAll(event.blockList().map { block ->
        BlockChange(
          cause = "explode",
          event = event,
          block = block,
          isBreak = true
        )
      })

      add(BlockChange(
        cause = "exploded",
        event = event,
        block = event.block,
        isBreak = true
      ))
    }

  fun blockBurn(event: BlockBurnEvent): BlockChange =
    BlockChange(
      playerUniqueIdentity = null,
      isBreak = true,
      cause = "burn",
      event = event
    )

  fun blockDamage(event: BlockDamageEvent): BlockChange =
    BlockChange(
      playerUniqueIdentity = null,
      cause = "damage",
      event = event
    )

  fun blockForm(event: BlockFormEvent): BlockChange =
    BlockChange(
      playerUniqueIdentity = null,
      cause = "form",
      event = event,
      state = event.newState
    )

  fun blockGrow(event: BlockGrowEvent): BlockChange =
    BlockChange(
      playerUniqueIdentity = null,
      cause = "grow",
      event = event,
      state = event.newState
    )

  fun blockFade(event: BlockFadeEvent): BlockChange =
    BlockChange(
      playerUniqueIdentity = null,
      cause = "fade",
      event = event,
      state = event.newState
    )

  fun blockIgnite(event: BlockIgniteEvent): BlockChange =
    BlockChange(
      playerUniqueIdentity = event.player?.uniqueId,
      cause = "ignite",
      event = event
    )

  fun blockSpread(event: BlockSpreadEvent): BlockChange =
    BlockChange(
      playerUniqueIdentity = null,
      cause = "spread",
      event = event,
      state = event.newState
    )

  fun fluidLevelChange(event: FluidLevelChangeEvent): BlockChange =
    BlockChange(
      playerUniqueIdentity = null,
      cause = "fluid-level-change",
      event = event,
      data = event.newData
    )

  fun signChange(event: SignChangeEvent): BlockChange =
    BlockChange(
      playerUniqueIdentity = event.player.uniqueId,
      cause = "sign-change",
      event = event
    )

  fun spongeAbsorb(event: SpongeAbsorbEvent): List<BlockChange> =
    event.blocks.map { block ->
      BlockChange(
        playerUniqueIdentity = null,
        event = event,
        cause = "sponge-absorb",
        block = block.block,
        state = block
      )
    }

  fun moistureChange(event: MoistureChangeEvent): BlockChange =
    BlockChange(
      playerUniqueIdentity = null,
      event = event,
      cause = "moisture-change",
      state = event.newState
    )

  fun blockCook(event: BlockCookEvent): BlockChange =
    BlockChange(
      playerUniqueIdentity = null,
      event = event,
      cause = "cook"
    )

  fun physics(event: BlockPhysicsEvent): BlockChange =
    BlockChange(
      playerUniqueIdentity = null,
      event = event,
      data = event.changedBlockData,
      cause = "physics"
    )
}

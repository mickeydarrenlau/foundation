package gay.pizza.foundation.chaos.modules

import org.bukkit.Chunk
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import java.util.WeakHashMap

class ChunkEnterRotate : ChaosModule {
  override fun id(): String = "chunk-enter-rotate"
  override fun name(): String = "Chunk Enter Rotate"
  override fun what(): String = "Rotates the chunk when the player enters a chunk."

  private val playerChunkMap = WeakHashMap<Player, Chunk>()

  @EventHandler
  fun onPotentialChunkMove(event: PlayerMoveEvent) {
    val player = event.player
    val currentChunk = event.player.chunk
    val previousChunk = playerChunkMap.put(player, currentChunk)
    if (previousChunk == null || previousChunk == currentChunk) {
      return
    }
    rotateChunk(currentChunk)
    if (!player.isFlying) {
      player.teleport(player.location.toHighestLocation().add(0.0, 1.0, 0.0))
    }
  }

  private fun rotateChunk(chunk: Chunk) {
    val snapshot = chunk.chunkSnapshot
    for (x in 0..15) {
      for (z in 0..15) {
        for (y in chunk.world.minHeight until chunk.world.maxHeight) {
          val rotatedBlock = chunk.getBlock(z, y, x)
          val originalBlockData = snapshot.getBlockData(x, y, z)
          rotatedBlock.blockData = originalBlockData
        }
      }
    }
  }
}

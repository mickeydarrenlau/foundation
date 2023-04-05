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
      player.teleportHighestLocation()
    }
  }

  private fun rotateChunk(chunk: Chunk) {
    val snapshot = chunk.chunkSnapshot
    chunk.forEachBlock { x, y, z, rotatedBlock ->
      val originalBlockData = snapshot.getBlockData(z, y, x)
      rotatedBlock.blockData = originalBlockData
    }
  }
}

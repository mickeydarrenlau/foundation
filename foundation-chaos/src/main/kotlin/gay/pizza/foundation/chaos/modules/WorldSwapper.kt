package gay.pizza.foundation.chaos.modules

import gay.pizza.foundation.chaos.randomPlayer
import org.bukkit.ChunkSnapshot
import org.bukkit.plugin.Plugin

class WorldSwapper(val plugin: Plugin) : ChaosModule {
  override fun id(): String = "world-swapper"
  override fun name(): String = "World Swapper"
  override fun what(): String = "Swaps the world vertically on activation, and un-swaps it on deactivation."

  var snapshot: ChunkSnapshot? = null

  override fun activate() {
    val player = plugin.server.randomPlayer() ?: return
    val chunk = player.world.getChunkAt(player.location)
    val localSnapshot = chunk.chunkSnapshot

    for (x in 0..15) {
      for (z in 0..15) {
        val heightRange = (chunk.world.minHeight + 1) until chunk.world.maxHeight
        for (y in heightRange) {
          val targetBlock = chunk.getBlock(x, y, z)
          val inverseY = heightRange.random()
          val nextBlock = localSnapshot.getBlockData(x, inverseY, z)
          targetBlock.setBlockData(nextBlock, true)
        }
      }
    }
    snapshot = localSnapshot
  }

  override fun deactivate() {
    val localSnapshot = snapshot ?: return
    val world = plugin.server.getWorld(localSnapshot.worldName) ?: return
    val chunk = world.getChunkAt(localSnapshot.x, localSnapshot.z)

    for (x in 0..15) {
      for (z in 0..15) {
        val heightRange = chunk.world.minHeight + 1 until chunk.world.maxHeight
        for (y in heightRange) {
          val originalBlock = localSnapshot.getBlockData(x, y, z)
          chunk.getBlock(x, y, z).blockData = originalBlock
        }
      }
    }
  }
}

package gay.pizza.foundation.chaos.modules

import gay.pizza.foundation.chaos.randomPlayer
import org.bukkit.Chunk
import org.bukkit.ChunkSnapshot
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.plugin.Plugin

class WorldSwapper(val plugin: Plugin) : ChaosModule {
  override fun id(): String = "world-swapper"
  override fun name(): String = "World Swapper"
  override fun what(): String = "Swaps the world vertically on activation, and un-swaps it on deactivation."

  var chunkInversions = mutableListOf<ChunkInversion>()

  override fun activate() {
    val player = plugin.server.randomPlayer() ?: return
    val baseChunk = player.chunk
    recordInvert(baseChunk)
    player.teleport(player.location.toHighestLocation())
    val chunksToInvert = player.world.loadedChunks.filter { it != baseChunk }.toMutableList()

    println("Inverting ${chunksToInvert.size} chunks...")
    fun scheduleOne() {
      if (chunksToInvert.isEmpty()) {
        return
      }

      val chunk = chunksToInvert.removeAt(0)
      plugin.server.scheduler.runTaskLater(plugin, { ->
        recordInvert(chunk)
        scheduleOne()
      }, 5)
    }

    scheduleOne()
  }

  fun recordInvert(chunk: Chunk) {
    chunkInversions.add(invertChunk(chunk))
  }

  override fun deactivate() {
    fun scheduleOne() {
      if (chunkInversions.isEmpty()) {
        return
      }

      val inversion = chunkInversions.removeAt(0)
      plugin.server.scheduler.runTaskLater(plugin, { ->
        inversion.revert()
        scheduleOne()
      }, 5)
    }

    scheduleOne()
  }

  fun invertChunk(chunk: Chunk): ChunkInversion {
    val snapshot = chunk.chunkSnapshot
    for (x in 0..15) {
      for (z in 0..15) {
        var sy = chunk.world.minHeight
        var ey = chunk.world.maxHeight
        while (sy != ey) {
          sy++
          ey--
          val targetBlock = chunk.getBlock(x, sy, z)
          val targetBlockData = targetBlock.blockData.clone()
          val nextBlock = chunk.getBlock(x, ey, z)
          val nextBlockData = nextBlock.blockData.clone()
          invertSetBlockData(targetBlock, nextBlockData)
          invertSetBlockData(nextBlock, targetBlockData)
        }
      }
    }
    return ChunkInversion(plugin, snapshot)
  }

  private fun invertSetBlockData(block: Block, data: BlockData) {
    block.setBlockData(data, false)
  }

  class ChunkInversion(
    val plugin: Plugin,
    val snapshot: ChunkSnapshot
  ) {
    fun revert() {
      val world = plugin.server.getWorld(snapshot.worldName) ?: return
      val chunk = world.getChunkAt(snapshot.x, snapshot.z)

      for (x in 0..15) {
        for (z in 0..15) {
          val heightRange = chunk.world.minHeight + 1 until chunk.world.maxHeight
          for (y in heightRange) {
            val originalBlock = snapshot.getBlockData(x, y, z)
            chunk.getBlock(x, y, z).blockData = originalBlock
          }
        }
      }
    }
  }
}

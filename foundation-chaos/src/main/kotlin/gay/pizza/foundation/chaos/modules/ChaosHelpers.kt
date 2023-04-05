package gay.pizza.foundation.chaos.modules

import org.bukkit.Chunk
import org.bukkit.ChunkSnapshot
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler

val World.heightRange
  get() = minHeight until maxHeight

inline fun forEachChunkPosition(crossinline each: (Int, Int) -> Unit) {
  for (x in 0..15) {
    for (z in 0..15) {
      each(x, z)
    }
  }
}

inline fun Chunk.forEachPosition(crossinline each: (Int, Int, Int) -> Unit) {
  for (x in 0..15) {
    for (z in 0..15) {
      for (y in world.heightRange) {
        each(x, y, z)
      }
    }
  }
}

inline fun Chunk.forEachBlock(crossinline each: (Int, Int, Int, Block) -> Unit) {
  forEachPosition { x, y, z ->
    each(x, y, z, getBlock(x, y, z))
  }
}

fun Chunk.applyChunkSnapshot(snapshot: ChunkSnapshot) {
  forEachPosition { x, y, z ->
    val blockData = snapshot.getBlockData(x, y, z)
    val block = getBlock(x, y, z)
    block.blockData = blockData
  }
}

fun Player.teleportHighestLocation() {
  teleport(location.toHighestLocation().add(0.0, 1.0, 0.0))
}

fun <T> BukkitScheduler.scheduleUntilEmpty(
  plugin: Plugin,
  items: MutableList<T>,
  ticksBetween: Long,
  callback: (T) -> Unit
) {
  fun performOne() {
    if (items.isEmpty()) {
      return
    }
    val item = items.removeAt(0)
    callback(item)

    if (items.isNotEmpty()) {
      runTaskLater(plugin, { ->
        performOne()
      }, ticksBetween)
    }
  }

  performOne()
}

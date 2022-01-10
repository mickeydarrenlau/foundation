package cloud.kubelet.foundation.gjallarhorn.state

import kotlin.math.absoluteValue

class BlockLogTracker(private val mode: BlockTrackMode = BlockTrackMode.RemoveOnDelete) {
  val blocks = HashMap<BlockCoordinate, BlockState>()

  fun place(position: BlockCoordinate, state: BlockState) {
    blocks[position] = state
  }

  fun delete(position: BlockCoordinate) {
    if (mode == BlockTrackMode.AirOnDelete) {
      blocks[position] = BlockState.AirBlock
    } else {
      blocks.remove(position)
    }
  }

  fun calculateZeroBlockOffset(): BlockCoordinate {
    val x = blocks.keys.minOf { it.x }
    val y = blocks.keys.minOf { it.y }
    val z = blocks.keys.minOf { it.z }

    val xOffset = if (x < 0) x.absoluteValue else 0
    val yOffset = if (y < 0) y.absoluteValue else 0
    val zOffset = if (z < 0) z.absoluteValue else 0

    return BlockCoordinate(xOffset, yOffset, zOffset)
  }

  fun calculateMaxBlock(): BlockCoordinate {
    val x = blocks.keys.maxOf { it.x }
    val y = blocks.keys.maxOf { it.y }
    val z = blocks.keys.maxOf { it.z }
    return BlockCoordinate(x, y, z)
  }

  fun isEmpty() = blocks.isEmpty()
  fun isNotEmpty() = !isEmpty()

  fun buildBlockMap(offset: BlockCoordinate = BlockCoordinate.zero): BlockMap {
    val map = BlockMap()
    blocks.forEach { (position, state) ->
      val realPosition = offset.applyAsOffset(position)
      map.put(realPosition, state)
    }
    return map
  }

  fun replay(changelog: BlockChangelog) = changelog.changes.forEach { change ->
    if (change.type == BlockChangeType.Break) {
      delete(change.location)
    } else {
      place(change.location, change.to)
    }
  }
}

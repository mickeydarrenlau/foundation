package cloud.kubelet.foundation.gjallarhorn.render

import kotlin.math.absoluteValue

class BlockStateTracker(private val mode: BlockTrackMode = BlockTrackMode.RemoveOnDelete) {
  val blocks = HashMap<BlockPosition, BlockState>()

  fun place(position: BlockPosition, state: BlockState) {
    blocks[position] = state
  }

  fun delete(position: BlockPosition) {
    if (mode == BlockTrackMode.AirOnDelete) {
      blocks[position] = BlockState("minecraft:air")
    } else {
      blocks.remove(position)
    }
  }

  fun calculateZeroBlockOffset(): BlockPosition {
    val x = blocks.keys.minOf { it.x }
    val y = blocks.keys.minOf { it.y }
    val z = blocks.keys.minOf { it.z }

    val xOffset = if (x < 0) x.absoluteValue else 0
    val yOffset = if (y < 0) y.absoluteValue else 0
    val zOffset = if (z < 0) z.absoluteValue else 0

    return BlockPosition(xOffset, yOffset, zOffset)
  }

  fun calculateMaxBlock(): BlockPosition {
    val x = blocks.keys.maxOf { it.x }
    val y = blocks.keys.maxOf { it.y }
    val z = blocks.keys.maxOf { it.z }
    return BlockPosition(x, y, z)
  }

  fun isEmpty() = blocks.isEmpty()

  fun populateStateImage(image: BlockStateImage, offset: BlockPosition = BlockPosition.zero) {
    blocks.forEach { (position, state) ->
      val realPosition = offset.applyAsOffset(position)
      image.put(realPosition, state)
    }
  }
}

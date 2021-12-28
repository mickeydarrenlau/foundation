package cloud.kubelet.foundation.gjallarhorn

import kotlin.collections.HashMap
import kotlin.math.absoluteValue

class BlockStateTracker {
  val blocks = HashMap<BlockPosition, BlockState>()

  fun place(position: BlockPosition, state: BlockState) {
    blocks[position] = state
  }

  fun delete(position: BlockPosition) {
    blocks.remove(position)
  }

  fun calculateZeroBlockOffset(): BlockOffset {
    val x = blocks.keys.minOf { it.x }
    val y = blocks.keys.minOf { it.y }
    val z = blocks.keys.minOf { it.z }

    val xOffset = if (x < 0) x.absoluteValue else 0
    val yOffset = if (y < 0) y.absoluteValue else 0
    val zOffset = if (z < 0) z.absoluteValue else 0

    return BlockOffset(xOffset, yOffset, zOffset)
  }

  fun populate(image: BlockStateImage, offset: BlockOffset = BlockOffset.none) {
    blocks.forEach { (position, state) ->
      val realPosition = offset.apply(position)
      image.put(realPosition, state)
    }
  }
}

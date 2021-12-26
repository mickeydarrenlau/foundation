package cloud.kubelet.foundation.gjallarhorn

import java.util.*
import kotlin.collections.HashMap

class BlockStateTracker {
  val blocks = HashMap<BlockPosition, BlockState>()

  fun place(position: BlockPosition, state: BlockState) {
    blocks[position] = state
  }

  fun delete(position: BlockPosition) {
    blocks.remove(position)
  }

  data class BlockState(val type: String)

  data class BlockPosition(
    val x: Long,
    val y: Long,
    val z: Long
  ) {
    override fun equals(other: Any?): Boolean {
      if (other !is BlockPosition) {
        return false
      }

      return other.x == x && other.y == y && other.z == z
    }

    override fun hashCode(): Int = Objects.hash(x, y, z)
  }
}

package cloud.kubelet.foundation.gjallarhorn.render

import java.util.*

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

  fun applyAsOffset(position: BlockPosition) = position.copy(
    x = position.x + x,
    y = position.y + y,
    z = position.z + z
  )

  companion object {
    val zero = BlockPosition(0, 0, 0)

    fun maxOf(positions: Sequence<BlockPosition>): BlockPosition {
      val x = positions.maxOf { it.x }
      val y = positions.maxOf { it.y }
      val z = positions.maxOf { it.z }

      return BlockPosition(x, y, z)
    }
  }
}

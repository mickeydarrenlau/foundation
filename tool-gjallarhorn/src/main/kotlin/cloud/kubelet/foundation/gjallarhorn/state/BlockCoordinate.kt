package cloud.kubelet.foundation.gjallarhorn.state

import java.util.*

data class BlockCoordinate(
  val x: Long,
  val y: Long,
  val z: Long
) {
  override fun equals(other: Any?): Boolean {
    if (other !is BlockCoordinate) {
      return false
    }

    return other.x == x && other.y == y && other.z == z
  }

  override fun hashCode(): Int = Objects.hash(x, y, z)

  fun applyAsOffset(coordinate: BlockCoordinate) = coordinate.copy(
    x = coordinate.x + x,
    y = coordinate.y + y,
    z = coordinate.z + z
  )

  companion object {
    val zero = BlockCoordinate(0, 0, 0)

    fun maxOf(coordinates: List<BlockCoordinate>): BlockCoordinate {
      val x = coordinates.maxOf { it.x }
      val y = coordinates.maxOf { it.y }
      val z = coordinates.maxOf { it.z }

      return BlockCoordinate(x, y, z)
    }
  }
}

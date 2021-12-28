package cloud.kubelet.foundation.gjallarhorn

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
}
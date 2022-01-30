package cloud.kubelet.foundation.gjallarhorn.state

import java.util.*

open class BlockCoordinateSparseMap<T> {
  val blocks = TreeMap<Long, TreeMap<Long, TreeMap<Long, T>>>()

  fun get(position: BlockCoordinate): T? = blocks[position.x]?.get(position.z)?.get(position.z)
  fun getVerticalSection(x: Long, z: Long): Map<Long, T>? = blocks[x]?.get(z)
  fun getXSection(x: Long): Map<Long, Map<Long, T>>? = blocks[x]

  fun put(position: BlockCoordinate, value: T) {
    blocks.getOrPut(position.x) {
      TreeMap()
    }.getOrPut(position.z) {
      TreeMap()
    }[position.y] = value
  }

  fun createOrModify(position: BlockCoordinate, create: () -> T, modify: (T) -> Unit) {
    val existing = get(position)
    if (existing == null) {
      put(position, create())
    } else {
      modify(existing)
    }
  }
}

package cloud.kubelet.foundation.gjallarhorn.state

import java.util.*

class BlockMap {
  val blocks = TreeMap<Long, TreeMap<Long, TreeMap<Long, BlockState>>>()

  fun put(position: BlockCoordinate, state: BlockState) {
    blocks.getOrPut(position.x) {
      TreeMap()
    }.getOrPut(position.z) {
      TreeMap()
    }[position.y] = state
  }
}

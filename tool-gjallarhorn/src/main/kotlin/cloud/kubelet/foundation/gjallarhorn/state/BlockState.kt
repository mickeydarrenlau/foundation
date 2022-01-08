package cloud.kubelet.foundation.gjallarhorn.state

import java.util.concurrent.ConcurrentHashMap

class BlockState(val type: String) {
  companion object {
    private val cache = ConcurrentHashMap<String, BlockState>()

    val AirBlock: BlockState = cached("minecraft:air")

    fun cached(type: String): BlockState = cache.computeIfAbsent(type) { BlockState(type) }
  }
}

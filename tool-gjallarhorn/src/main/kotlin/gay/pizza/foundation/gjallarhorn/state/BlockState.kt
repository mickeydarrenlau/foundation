package gay.pizza.foundation.gjallarhorn.state

import kotlinx.serialization.Serializable
import java.util.concurrent.ConcurrentHashMap

@Serializable(BlockStateSerializer::class)
data class BlockState(val type: String) {
  companion object {
    private val cache = ConcurrentHashMap<String, BlockState>()

    val AirBlock: BlockState = cached("minecraft:air")

    fun cached(type: String): BlockState = cache.computeIfAbsent(type) { BlockState(type) }
  }
}

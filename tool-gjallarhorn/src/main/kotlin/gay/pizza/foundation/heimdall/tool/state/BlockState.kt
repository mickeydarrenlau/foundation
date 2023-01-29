package gay.pizza.foundation.heimdall.tool.state

import java.util.concurrent.ConcurrentHashMap
import kotlinx.serialization.Serializable

@Serializable(BlockStateSerializer::class)
data class BlockState(val type: String) {
  companion object {
    private val cache = ConcurrentHashMap<String, BlockState>()

    val AirBlock: BlockState = cached("minecraft:air")

    fun cached(type: String): BlockState = cache.computeIfAbsent(type) { BlockState(type) }
  }
}

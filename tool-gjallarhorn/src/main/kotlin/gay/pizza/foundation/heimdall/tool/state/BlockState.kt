package gay.pizza.foundation.heimdall.tool.state

import kotlinx.serialization.Serializable

@Serializable
data class BlockState(
  val type: String,
  val data: String? = null
) {
  companion object {
    val AirBlock: BlockState = BlockState("minecraft:air")
  }
}

package gay.pizza.foundation.heimdall.tool.state

import kotlinx.serialization.Serializable

@Serializable
enum class BlockChangeType {
  Place,
  Break
}

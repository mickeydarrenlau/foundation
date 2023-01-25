package gay.pizza.foundation.gjallarhorn.state

import kotlinx.serialization.Serializable

@Serializable
enum class BlockChangeType {
  Place,
  Break
}

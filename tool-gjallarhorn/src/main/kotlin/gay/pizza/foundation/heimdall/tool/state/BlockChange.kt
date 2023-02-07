package gay.pizza.foundation.heimdall.tool.state

import java.time.Instant
import java.util.UUID

data class BlockChange(
  val time: Instant,
  val world: UUID,
  val type: BlockChangeType,
  val location: BlockCoordinate,
  val from: BlockState,
  val to: BlockState
)

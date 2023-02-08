package gay.pizza.foundation.heimdall.tool.state

import java.time.Instant
import java.util.UUID

data class RecordedBlockChange(
  val time: Instant,
  val world: UUID,
  val location: BlockCoordinate,
  val state: BlockState
)

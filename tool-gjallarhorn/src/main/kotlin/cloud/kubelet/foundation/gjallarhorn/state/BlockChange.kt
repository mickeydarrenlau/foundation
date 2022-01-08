package cloud.kubelet.foundation.gjallarhorn.state

import java.time.Instant

data class BlockChange(
  val time: Instant,
  val type: BlockChangeType,
  val location: BlockCoordinate,
  val from: BlockState,
  val to: BlockState
)

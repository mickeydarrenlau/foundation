package cloud.kubelet.foundation.gjallarhorn.state

import java.time.Instant
import java.util.*

data class PlayerPositionChange(
  val time: Instant,
  val player: UUID,
  val world: UUID,
  val x: Double,
  val y: Double,
  val z: Double,
  val pitch: Double,
  val yaw: Double
)

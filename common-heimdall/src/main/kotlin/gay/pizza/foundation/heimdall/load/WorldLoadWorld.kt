package gay.pizza.foundation.heimdall.load

import gay.pizza.foundation.heimdall.export.ExportedBlock
import kotlinx.serialization.Serializable

@Serializable
class WorldLoadWorld(
  val name: String,
  val blocks: Map<Long, Map<Long, Map<Long, ExportedBlock>>>
)

package gay.pizza.foundation.heimdall.load

import gay.pizza.foundation.heimdall.export.ExportedBlock
import kotlinx.serialization.Serializable

@Serializable
class WorldLoadFormat(
  val blockLookupTable: List<ExportedBlock>,
  val worlds: Map<String, WorldLoadWorld>
)

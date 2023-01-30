package gay.pizza.foundation.heimdall.export

import kotlinx.serialization.Serializable

@Serializable
data class ExportedChunk(
  val blocks: List<ExportedBlock>,
  val x: Int,
  val z: Int,
  val sections: List<ExportedChunkSection>
)

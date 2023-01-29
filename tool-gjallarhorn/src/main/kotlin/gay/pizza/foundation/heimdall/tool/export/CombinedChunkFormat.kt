package gay.pizza.foundation.heimdall.tool.export

import gay.pizza.foundation.heimdall.tool.state.BlockExpanse
import gay.pizza.foundation.heimdall.tool.state.SparseBlockStateMap
import kotlinx.serialization.Serializable

@Serializable
class CombinedChunkFormat(
  val expanse: BlockExpanse,
  val map: SparseBlockStateMap
)

package gay.pizza.foundation.gjallarhorn.export

import gay.pizza.foundation.gjallarhorn.state.BlockExpanse
import gay.pizza.foundation.gjallarhorn.state.SparseBlockStateMap
import kotlinx.serialization.Serializable

@Serializable
class CombinedChunkFormat(
  val expanse: BlockExpanse,
  val map: SparseBlockStateMap
)

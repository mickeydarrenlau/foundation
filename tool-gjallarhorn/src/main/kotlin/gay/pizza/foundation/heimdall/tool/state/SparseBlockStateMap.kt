package gay.pizza.foundation.heimdall.tool.state

import kotlinx.serialization.Serializable

@Serializable(SparseBlockStateMapSerializer::class)
class SparseBlockStateMap(blocks: Map<Long, Map<Long, Map<Long, BlockState>>> = mutableMapOf()) :
  BlockCoordinateSparseMap<BlockState>(blocks)

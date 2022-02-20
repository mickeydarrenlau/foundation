package cloud.kubelet.foundation.gjallarhorn.state

import kotlinx.serialization.Serializable

@Serializable(SparseBlockStateMapSerializer::class)
class SparseBlockStateMap(blocks: Map<Long, Map<Long, Map<Long, BlockState>>> = mutableMapOf()) :
  BlockCoordinateSparseMap<BlockState>(blocks)

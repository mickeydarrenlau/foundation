package cloud.kubelet.foundation.gjallarhorn.export

import cloud.kubelet.foundation.gjallarhorn.state.BlockExpanse
import cloud.kubelet.foundation.gjallarhorn.state.SparseBlockStateMap

@kotlinx.serialization.Serializable
class CombinedChunkFormat(
  val expanse: BlockExpanse,
  val map: SparseBlockStateMap
)

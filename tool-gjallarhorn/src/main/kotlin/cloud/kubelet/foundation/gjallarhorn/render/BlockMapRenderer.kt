package cloud.kubelet.foundation.gjallarhorn.render

import cloud.kubelet.foundation.gjallarhorn.state.BlockStateMap
import cloud.kubelet.foundation.gjallarhorn.state.ChangelogSlice

interface BlockMapRenderer<T> {
  fun render(slice: ChangelogSlice, map: BlockStateMap): T
}

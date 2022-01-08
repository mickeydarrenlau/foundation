package cloud.kubelet.foundation.gjallarhorn.render

import cloud.kubelet.foundation.gjallarhorn.state.BlockMap

interface BlockMapRenderer<T> {
  fun render(map: BlockMap): T
}

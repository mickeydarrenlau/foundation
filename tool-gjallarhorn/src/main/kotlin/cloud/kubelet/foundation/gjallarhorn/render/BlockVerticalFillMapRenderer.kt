package cloud.kubelet.foundation.gjallarhorn.render

import cloud.kubelet.foundation.gjallarhorn.state.BlockExpanse
import cloud.kubelet.foundation.gjallarhorn.state.BlockStateMap
import cloud.kubelet.foundation.gjallarhorn.state.SparseBlockStateMap
import cloud.kubelet.foundation.gjallarhorn.state.ChangelogSlice
import cloud.kubelet.foundation.gjallarhorn.util.FloatClamp
import java.awt.image.BufferedImage

class BlockVerticalFillMapRenderer(val expanse: BlockExpanse, quadPixelSize: Int = defaultQuadPixelSize) :
  BlockHeatMapRenderer(quadPixelSize) {
  override fun render(slice: ChangelogSlice, map: BlockStateMap): BufferedImage {
    val blockMap = map as SparseBlockStateMap
    val yMin = blockMap.blocks.minOf { xSection -> xSection.value.minOf { zSection -> zSection.value.size } }
    val yMax = blockMap.blocks.maxOf { xSection -> xSection.value.maxOf { zSection -> zSection.value.size } }
    val clamp = FloatClamp(yMin.toLong(), yMax.toLong())

    return buildHeatMapImage(expanse, clamp) { x, z -> blockMap.blocks[x]?.get(z)?.maxOf { it.key } }
  }
}

package gay.pizza.foundation.heimdall.tool.render

import gay.pizza.foundation.heimdall.tool.state.BlockExpanse
import gay.pizza.foundation.heimdall.tool.state.BlockStateMap
import gay.pizza.foundation.heimdall.tool.state.SparseBlockStateMap
import gay.pizza.foundation.heimdall.tool.state.ChangelogSlice
import gay.pizza.foundation.heimdall.tool.util.FloatClamp
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

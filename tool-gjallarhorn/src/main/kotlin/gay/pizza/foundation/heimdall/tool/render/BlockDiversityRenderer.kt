package gay.pizza.foundation.heimdall.tool.render

import gay.pizza.foundation.heimdall.tool.state.BlockExpanse
import gay.pizza.foundation.heimdall.tool.state.BlockStateMap
import gay.pizza.foundation.heimdall.tool.state.ChangelogSlice
import gay.pizza.foundation.heimdall.tool.util.BlockColorKey
import gay.pizza.foundation.heimdall.tool.util.defaultBlockColorMap
import java.awt.Color
import java.awt.image.BufferedImage

class BlockDiversityRenderer(val expanse: BlockExpanse, quadPixelSize: Int = defaultQuadPixelSize) :
  BlockGridRenderer(quadPixelSize) {
  private val blockColorKey = BlockColorKey(defaultBlockColorMap)

  override fun render(slice: ChangelogSlice, map: BlockStateMap): BufferedImage = buildPixelQuadImage(expanse) { graphics, x, z ->
    val maybeYBlocks = map.getVerticalSection(x, z)
    if (maybeYBlocks == null) {
      setPixelQuad(graphics, x, z, Color.white)
      return@buildPixelQuadImage
    }
    val maxBlockState = maybeYBlocks.maxByOrNull { it.key }?.value
    if (maxBlockState == null) {
      setPixelQuad(graphics, x, z, Color.white)
      return@buildPixelQuadImage
    }

    val color = blockColorKey.map(maxBlockState.type)
    setPixelQuad(graphics, x, z, color)
  }
}

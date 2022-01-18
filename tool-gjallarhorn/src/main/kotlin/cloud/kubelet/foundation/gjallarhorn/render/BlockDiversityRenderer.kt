package cloud.kubelet.foundation.gjallarhorn.render

import cloud.kubelet.foundation.gjallarhorn.state.BlockExpanse
import cloud.kubelet.foundation.gjallarhorn.state.BlockMap
import cloud.kubelet.foundation.gjallarhorn.state.ChangelogSlice
import cloud.kubelet.foundation.gjallarhorn.util.BlockColorKey
import cloud.kubelet.foundation.gjallarhorn.util.defaultBlockColorMap
import java.awt.Color
import java.awt.image.BufferedImage

class BlockDiversityRenderer(val expanse: BlockExpanse, quadPixelSize: Int = defaultQuadPixelSize) :
  BlockGridRenderer(quadPixelSize) {
  private val blockColorKey = BlockColorKey(defaultBlockColorMap)

  override fun render(slice: ChangelogSlice, map: BlockMap): BufferedImage = buildPixelQuadImage(expanse) { graphics, x, z ->
    val maybeYBlocks = map.blocks[x]?.get(z)
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

package cloud.kubelet.foundation.gjallarhorn.render

import cloud.kubelet.foundation.gjallarhorn.util.ColorGradient
import cloud.kubelet.foundation.gjallarhorn.util.FloatClamp
import cloud.kubelet.foundation.gjallarhorn.util.RandomColorKey
import java.awt.Color
import java.awt.image.BufferedImage
import java.util.*

class BlockStateImage {
  private val blocks = TreeMap<Long, TreeMap<Long, TreeMap<Long, BlockState>>>()

  fun put(position: BlockPosition, state: BlockState) {
    blocks.getOrPut(position.x) {
      TreeMap()
    }.getOrPut(position.z) {
      TreeMap()
    }[position.y] = state
  }

  fun buildTopDownImage(expanse: BlockExpanse): BufferedImage {
    val colorKey = RandomColorKey()
    return buildPixelQuadImage(expanse) { x, z ->
      val maybeYBlocks = blocks[x]?.get(z)
      if (maybeYBlocks == null) {
        setPixelQuad(x, z, Color.white.rgb)
        return@buildPixelQuadImage
      }
      val maxBlockState = maybeYBlocks.maxByOrNull { it.key }?.value
      if (maxBlockState == null) {
        setPixelQuad(x, z, Color.white.rgb)
        return@buildPixelQuadImage
      }

      val color = colorKey.map(maxBlockState.type)
      setPixelQuad(x, z, color.rgb)
    }
  }

  fun buildHeightMapImage(expanse: BlockExpanse): BufferedImage {
    val yMin = blocks.minOf { xSection -> xSection.value.minOf { zSection -> zSection.value.minOf { it.key } } }
    val yMax = blocks.maxOf { xSection -> xSection.value.maxOf { zSection -> zSection.value.maxOf { it.key } } }
    val clamp = FloatClamp(yMin, yMax)

    return buildHeatMapImage(expanse, clamp) { x, z -> blocks[x]?.get(z)?.maxOf { it.key } }
  }

  fun buildHeatMapImage(expanse: BlockExpanse, clamp: FloatClamp, calculate: (Long, Long) -> Long?): BufferedImage =
    buildPixelQuadImage(expanse) { x, z ->
      val value = calculate(x, z)
      val color = if (value != null) {
        val floatValue = clamp.convert(value)
        ColorGradient.HeatMap.getColorAtValue(floatValue)
      } else {
        Color.white
      }

      setPixelQuad(x, z, color.rgb)
    }

  private fun BufferedImage.setPixelQuad(x: Long, z: Long, rgb: Int) {
    setRGB(x.toInt() * 2, z.toInt() * 2, rgb)
    setRGB((x.toInt() * 2) + 1, z.toInt() * 2, rgb)
    setRGB(x.toInt() * 2, (z.toInt() * 2) + 1, rgb)
    setRGB((x.toInt() * 2) + 1, (z.toInt() * 2) + 1, rgb)
  }

  private fun buildPixelQuadImage(expanse: BlockExpanse, callback: BufferedImage.(Long, Long) -> Unit): BufferedImage {
    val width = expanse.size.x
    val height = expanse.size.z
    val bufferedImage = BufferedImage(width.toInt() * 2, height.toInt() * 2, BufferedImage.TYPE_4BYTE_ABGR)

    for (x in 0 until width) {
      for (z in 0 until height) {
        callback(bufferedImage, x, z)
      }
    }
    return bufferedImage
  }
}

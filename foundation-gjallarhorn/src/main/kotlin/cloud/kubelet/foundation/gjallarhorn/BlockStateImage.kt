package cloud.kubelet.foundation.gjallarhorn

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

  fun buildTopDownImage(): BufferedImage {
    val colorKey = RandomColorKey()
    return buildPixelQuadImage { x, z ->
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

  fun buildHeightMapImage(): BufferedImage {
    val yMin = blocks.minOf { xSection -> xSection.value.minOf { zSection -> zSection.value.minOf { it.key } } }
    val yMax = blocks.maxOf { xSection -> xSection.value.maxOf { zSection -> zSection.value.maxOf { it.key } } }
    val clamp = FloatClamp(yMin, yMax)

    return buildHeatMapImage(clamp) { x, z -> blocks[x]?.get(z)?.maxOf { it.key } }
  }

  fun buildHeatMapImage(clamp: FloatClamp, calculate: (Long, Long) -> Long?): BufferedImage =
    buildPixelQuadImage { x, z ->
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

  private fun buildPixelQuadImage(callback: BufferedImage.(Long, Long) -> Unit): BufferedImage {
    val xMax = blocks.keys.maxOf { it }
    val zMax = blocks.maxOf { xSection -> xSection.value.maxOf { zSection -> zSection.key } }
    val bufferedImage = BufferedImage(xMax.toInt() * 2, zMax.toInt() * 2, BufferedImage.TYPE_4BYTE_ABGR)

    for (x in 0 until xMax) {
      for (z in 0 until zMax) {
        callback(bufferedImage, x, z)
      }
    }
    return bufferedImage
  }
}

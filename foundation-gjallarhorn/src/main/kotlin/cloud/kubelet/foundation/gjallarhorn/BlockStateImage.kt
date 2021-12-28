package cloud.kubelet.foundation.gjallarhorn

import cloud.kubelet.foundation.gjallarhorn.util.RandomColorKey
import java.awt.Color
import java.awt.image.BufferedImage
import java.util.*

class BlockStateImage {
  val blocks = TreeMap<Long, TreeMap<Long, TreeMap<Long, BlockState>>>()

  fun put(position: BlockPosition, state: BlockState) {
    blocks.getOrPut(position.x) {
      TreeMap()
    }.getOrPut(position.z) {
      TreeMap()
    }[position.y] = state
  }

  fun buildBufferedImage(): BufferedImage {
    val colorKey = RandomColorKey()
    val xMax = blocks.keys.maxOf { it }
    val zMax = blocks.maxOf { it.value.maxOf { it.key } }

    val bufferedImage = BufferedImage(xMax.toInt() * 2, zMax.toInt() * 2, BufferedImage.TYPE_4BYTE_ABGR)
    for (x in 0 until xMax) {
      for (z in 0 until zMax) {
        fun set(rgb: Int) {
          bufferedImage.setRGB(x.toInt() * 2, z.toInt() * 2, rgb)
          bufferedImage.setRGB((x.toInt() * 2) + 1, z.toInt() * 2, rgb)
          bufferedImage.setRGB(x.toInt() * 2, (z.toInt() * 2) + 1, rgb)
          bufferedImage.setRGB((x.toInt() * 2) + 1, (z.toInt() * 2) + 1, rgb)
        }

        val maybeYBlocks = blocks[x]?.get(z)
        if (maybeYBlocks == null) {
          set(Color.white.rgb)
          continue
        }
        val maxBlockState = maybeYBlocks.maxByOrNull { it.key }?.value
        if (maxBlockState == null) {
          set(Color.white.rgb)
          continue
        }

        val color = colorKey.map(maxBlockState.type)
        set(color.rgb)
      }
    }
    return bufferedImage
  }
}

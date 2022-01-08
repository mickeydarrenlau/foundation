package cloud.kubelet.foundation.gjallarhorn.render

import cloud.kubelet.foundation.gjallarhorn.state.BlockExpanse
import java.awt.Color
import java.awt.Rectangle
import java.awt.image.BufferedImage

abstract class BlockGridRenderer(val quadPixelSize: Int = defaultQuadPixelSize) : BlockImageRenderer {
  protected fun BufferedImage.setPixelQuad(x: Long, z: Long, rgb: Int) {
    drawSquare(x * quadPixelSize, z * quadPixelSize, quadPixelSize.toLong(), rgb)
  }

  protected fun BufferedImage.drawSquare(x: Long, y: Long, side: Long, rgb: Int) {
    val graphics = createGraphics()
    graphics.color = Color(rgb)
    graphics.fill(Rectangle(x.toInt(), y.toInt(), side.toInt(), side.toInt()))
    graphics.dispose()
  }

  protected fun buildPixelQuadImage(
    expanse: BlockExpanse,
    callback: BufferedImage.(Long, Long) -> Unit
  ): BufferedImage {
    val widthInBlocks = expanse.size.x
    val heightInBlocks = expanse.size.z
    val widthInPixels = widthInBlocks.toInt() * quadPixelSize
    val heightInPixels = heightInBlocks.toInt() * quadPixelSize
    val bufferedImage =
      BufferedImage(widthInPixels, heightInPixels, BufferedImage.TYPE_4BYTE_ABGR)

    for (x in 0 until widthInBlocks) {
      for (z in 0 until heightInBlocks) {
        callback(bufferedImage, x, z)
      }
    }
    return bufferedImage
  }

  companion object {
    const val defaultQuadPixelSize = 4
  }
}

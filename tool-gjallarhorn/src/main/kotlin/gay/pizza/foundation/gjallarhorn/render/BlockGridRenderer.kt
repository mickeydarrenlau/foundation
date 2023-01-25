package gay.pizza.foundation.gjallarhorn.render

import gay.pizza.foundation.gjallarhorn.state.BlockExpanse
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage

abstract class BlockGridRenderer(val quadPixelSize: Int = defaultQuadPixelSize) : BlockImageRenderer {
  protected fun setPixelQuad(graphics: Graphics2D, x: Long, z: Long, color: Color) {
    if (globalQuadPixelNoop) {
      return
    }
    drawSquare(graphics, x * quadPixelSize, z * quadPixelSize, quadPixelSize.toLong(), color)
  }

  protected fun drawSquare(graphics: Graphics2D, x: Long, y: Long, side: Long, color: Color) {
    graphics.color = color
    graphics.fill(Rectangle(x.toInt(), y.toInt(), side.toInt(), side.toInt()))
  }

  protected fun buildPixelQuadImage(
    expanse: BlockExpanse,
    callback: BufferedImage.(Graphics2D, Long, Long) -> Unit
  ): BufferedImage {
    val widthInBlocks = expanse.size.x
    val heightInBlocks = expanse.size.z
    val widthInPixels = widthInBlocks.toInt() * quadPixelSize
    val heightInPixels = heightInBlocks.toInt() * quadPixelSize
    val bufferedImage =
      BufferedImage(widthInPixels, heightInPixels, BufferedImage.TYPE_3BYTE_BGR)

    val graphics = bufferedImage.createGraphics()
    for (x in 0 until widthInBlocks) {
      for (z in 0 until heightInBlocks) {
        callback(bufferedImage, graphics, x, z)
      }
    }
    graphics.dispose()
    return bufferedImage
  }

  companion object {
    const val defaultQuadPixelSize = 4
    var globalQuadPixelNoop = false
  }
}

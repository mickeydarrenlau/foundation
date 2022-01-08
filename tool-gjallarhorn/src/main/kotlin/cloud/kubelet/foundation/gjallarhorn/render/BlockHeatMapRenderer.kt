package cloud.kubelet.foundation.gjallarhorn.render

import cloud.kubelet.foundation.gjallarhorn.state.BlockExpanse
import cloud.kubelet.foundation.gjallarhorn.util.ColorGradient
import cloud.kubelet.foundation.gjallarhorn.util.FloatClamp
import java.awt.Color
import java.awt.image.BufferedImage

abstract class BlockHeatMapRenderer(quadPixelSize: Int = defaultQuadPixelSize) : BlockGridRenderer(quadPixelSize) {
  protected fun buildHeatMapImage(
    expanse: BlockExpanse,
    clamp: FloatClamp,
    calculate: (Long, Long) -> Long?
  ): BufferedImage =
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
}

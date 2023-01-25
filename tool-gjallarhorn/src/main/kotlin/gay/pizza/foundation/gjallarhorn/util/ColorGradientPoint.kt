package gay.pizza.foundation.gjallarhorn.util

import java.awt.Color

data class ColorGradientPoint(
  val r: Float,
  val g: Float,
  val b: Float,
  val value: Float
) {
  fun toColor() = Color(
    FloatClamp.ColorRgbComponent.convert(r).toInt(),
    FloatClamp.ColorRgbComponent.convert(g).toInt(),
    FloatClamp.ColorRgbComponent.convert(b).toInt()
  )
}

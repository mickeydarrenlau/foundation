package gay.pizza.foundation.gjallarhorn.util

import java.awt.Color
import kotlin.math.max

class ColorGradient constructor() {
  constructor(vararg points: ColorGradientPoint) : this() {
    for (point in points) {
      addColorPoint(point)
    }
  }

  private val points = mutableListOf<ColorGradientPoint>()

  fun addColorPoint(point: ColorGradientPoint) {
    for (x in 0 until points.size) {
      if (point.value < points[x].value) {
        points.add(x, point)
        return
      }
    }
    points.add(point)
  }

  fun getColorAtValue(value: Float): Color {
    if (points.isEmpty()) {
      return ColorGradientPoint(0f, 0f, 0f, value).toColor()
    }

    for (x in 0 until points.size) {
      val current = points[x]
      if (value < current.value) {
        val previous = points[max(0, x - 1)]
        val diff = previous.value - current.value
        val fractionBetween = if (diff == 0f) 0f else (value - current.value) / diff
        return ColorGradientPoint(
          (previous.r - current.r) * fractionBetween + current.r,
          (previous.g - current.g) * fractionBetween + current.g,
          (previous.b - current.b) * fractionBetween + current.b,
          value
        ).toColor()
      }
    }

    return points.last().copy(value = value).toColor()
  }

  companion object {
    val HeatMap = ColorGradient(
      ColorGradientPoint(0f, 0f, 1f, 0.0f),
      ColorGradientPoint(0f, 1f, 1f, 0.25f),
      ColorGradientPoint(0f, 1f, 0f, 0.5f),
      ColorGradientPoint(1f, 1f, 0f, 0.75f),
      ColorGradientPoint(1f, 0f, 0f, 1.0f)
    )
  }
}

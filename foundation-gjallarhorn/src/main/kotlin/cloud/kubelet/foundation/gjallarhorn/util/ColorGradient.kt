package cloud.kubelet.foundation.gjallarhorn.util

import java.awt.Color
import kotlin.math.max

class ColorGradient {
  data class ColorPoint(
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

  private val points = mutableListOf<ColorPoint>()

  fun addColorPoint(red: Float, green: Float, blue: Float, value: Float) {
    val point = ColorPoint(red, green, blue, value)
    for (x in 0 until points.size) {
      if (value < points[x].value) {
        points.add(x, point)
        return
      }
    }
    points.add(point)
  }

  fun getColorAtValue(value: Float): Color {
    if (points.isEmpty()) {
      return ColorPoint(0f, 0f, 0f, value).toColor()
    }

    for (x in 0 until points.size) {
      val current = points[x]
      if (value < current.value) {
        val previous = points[max(0, x - 1)]
        val diff = previous.value - current.value
        val fractionBetween = if (diff == 0f) 0f else (value - current.value) / diff
        return ColorPoint(
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
    val HeatMap = ColorGradient().apply {
      addColorPoint(0f, 0f, 1f, 0.0f)
      addColorPoint(0f, 1f, 1f, 0.25f)
      addColorPoint(0f, 1f, 0f, 0.5f)
      addColorPoint(1f, 1f, 0f, 0.75f)
      addColorPoint(1f, 0f, 0f, 1.0f)
    }
  }
}

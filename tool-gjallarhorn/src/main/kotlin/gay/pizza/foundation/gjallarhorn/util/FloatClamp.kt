package gay.pizza.foundation.gjallarhorn.util

import kotlin.math.roundToLong

class FloatClamp(val min: Long, val max: Long) {
  fun convert(value: Float): Long = (value * max.toFloat()).roundToLong() + min
  fun convert(value: Long): Float = (value - min.toFloat()) / max

  companion object {
    val ColorRgbComponent = FloatClamp(0, 255)
  }
}

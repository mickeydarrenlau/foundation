package gay.pizza.foundation.gjallarhorn.util

import java.awt.Color
import java.util.concurrent.ConcurrentHashMap

class BlockColorKey(assigned: Map<String, Color>) {
  private val colors = ConcurrentHashMap(assigned)

  fun map(key: String): Color = colors.computeIfAbsent(key) { findUniqueColor() }

  private fun findUniqueColor(): Color {
    var random = randomColor()
    while (colors.values.any { it.rgb == random.rgb }) {
      random = randomColor()
    }
    return random
  }

  private fun randomColor() = Color((Math.random() * 0x1000000).toInt())
}

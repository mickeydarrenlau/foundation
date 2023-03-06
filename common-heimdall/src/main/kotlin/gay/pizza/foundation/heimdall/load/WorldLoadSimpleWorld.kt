package gay.pizza.foundation.heimdall.load

import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

@Serializable
class WorldLoadSimpleWorld(
  override val name: String,
  val blocks: Map<Long, Map<Long, Map<Long, Int>>>
) : WorldLoadWorld() {
  fun compact(): WorldLoadCompactWorld {
    val list = OffsetList.transform(
      blocks,
      minAndTotal = ::minAndTotal,
      keyToInt = Long::toInt,
      valueTransform = { zValue ->
        OffsetList.transform(
          zValue,
          minAndTotal = ::minAndTotal,
          keyToInt = Long::toInt,
          valueTransform = { yValue ->
            OffsetList.transform(
              yValue,
              minAndTotal = ::minAndTotal,
              keyToInt = Long::toInt,
              valueTransform = { it }
            )
          }
        )
      })
    return WorldLoadCompactWorld(name, list)
  }

  private fun <T> minAndTotal(map: Map<Long, T>): Pair<Int, Int> {
    val keys = map.keys

    if (keys.isEmpty()) {
      return 0 to 0
    }

    val min = keys.min()
    val max = keys.max()
    var total = 1L

    if (max > 0) {
      total += max
    }

    if (min < 0) {
      total += min.absoluteValue
    }
    return min.toInt() to total.toInt()
  }

  override fun crawl(block: (Long, Long, Long, Int) -> Unit) {
    for ((x, zBlocks) in blocks) {
      for ((z, yBlocks) in zBlocks) {
        for ((y, index) in yBlocks) {
          block(x, z, y, index)
        }
      }
    }
  }
}

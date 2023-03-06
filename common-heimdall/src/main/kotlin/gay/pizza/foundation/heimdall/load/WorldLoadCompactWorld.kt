package gay.pizza.foundation.heimdall.load

import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

@Serializable
data class OffsetList<L: List<T>, T>(
  val offset: Int,
  val data: L
) {
  fun <K> toMap(toKey: (Int) -> K): Map<K, T> {
    val map = mutableMapOf<K, T>()
    for ((index, value) in data.withIndex()) {
      val real = index + offset
      val key = toKey(real)
      map[key] = value
    }
    return map
  }

  fun <R> map(value: (T) -> R): ImmutableOffsetList<R> =
    ImmutableOffsetList(offset, MutableList(data.size) { index -> value(data[index]) })

  fun eachRealIndex(block: (Int, T) -> Unit) {
    for ((fakeIndex, value) in data.withIndex()) {
      val realIndex = fakeIndex + offset
      block(realIndex, value)
    }
  }

  companion object {
    fun <K, T, V> transform(
      map: Map<K, T>,
      minAndTotal: (Map<K, T>) -> Pair<Int, Int>,
      keyToInt: (K) -> Int,
      valueTransform: (T) -> V
    ): ImmutableOffsetList<V?> {
      val (min, total) = minAndTotal(map)
      val offset = if (min < 0) min.absoluteValue else 0
      val list = MutableList<V?>(total) { null }
      for ((key, value) in map) {
        val pkey = keyToInt(key)
        val rkey = pkey + offset
          list[rkey] = valueTransform(value)
      }
      return OffsetList(if (min < 0) min else 0, list)
    }
  }
}

typealias ImmutableOffsetList<T> = OffsetList<List<T>, T>

@Serializable
class WorldLoadCompactWorld(
  override val name: String,
  val data: ImmutableOffsetList<ImmutableOffsetList<ImmutableOffsetList<Int?>?>?>
) : WorldLoadWorld() {
  override fun crawl(block: (Long, Long, Long, Int) -> Unit) {
    data.eachRealIndex { x, zList ->
      zList?.eachRealIndex { z, yList ->
        yList?.eachRealIndex { y, index ->
          if (index != null) {
            block(x.toLong(), z.toLong(), y.toLong(), index)
          }
        }
      }
    }
  }
}

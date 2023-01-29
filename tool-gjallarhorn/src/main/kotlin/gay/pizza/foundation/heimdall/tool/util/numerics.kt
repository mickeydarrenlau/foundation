package gay.pizza.foundation.heimdall.tool.util

fun <T> Iterable<T>.minOfAll(fieldCount: Int, block: (value: T) -> List<Long>): List<Long> {
  val fieldRange = 0 until fieldCount
  val results = fieldRange.map { Long.MAX_VALUE }.toMutableList()
  for (item in this) {
    val numerics = block(item)
    for (field in fieldRange) {
      val current = results[field]
      val number = numerics[field]
      if (number < current) {
        results[field] = number
      }
    }
  }
  return results
}

fun <T> Iterable<T>.maxOfAll(fieldCount: Int, block: (value: T) -> List<Long>): List<Long> {
  val fieldRange = 0 until fieldCount
  val results = fieldRange.map { Long.MIN_VALUE }.toMutableList()
  for (item in this) {
    val numerics = block(item)
    for (field in fieldRange) {
      val current = results[field]
      val number = numerics[field]
      if (number > current) {
        results[field] = number
      }
    }
  }
  return results
}

fun <T> Sequence<T>.minOfAll(fieldCount: Int, block: (value: T) -> List<Long>): List<Long> =
  asIterable().minOfAll(fieldCount, block)

fun <T> Sequence<T>.maxOfAll(fieldCount: Int, block: (value: T) -> List<Long>): List<Long> =
  asIterable().maxOfAll(fieldCount, block)

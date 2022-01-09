package cloud.kubelet.foundation.gjallarhorn.state

import java.time.Duration
import java.time.Instant

data class BlockChangelogSlice(val from: Instant, val to: Instant, val relative: Duration) {
  constructor(from: Instant, to: Instant) : this(from, to, Duration.ofMillis(to.toEpochMilli() - from.toEpochMilli()))

  fun changeResolutionTime(): Instant = to.minus(relative)

  fun isTimeWithin(time: Instant) = time in from..to
  fun isRelativeWithin(time: Instant) = time in changeResolutionTime()..to

  fun split(): List<BlockChangelogSlice> {
    val half = relative.dividedBy(2)
    val initial = to.minus(relative)
    val first = initial.plus(half)
    return listOf(
      BlockChangelogSlice(from, first, half),
      BlockChangelogSlice(from, to, half)
    )
  }
}

package cloud.kubelet.foundation.gjallarhorn.state

import java.time.Duration
import java.time.Instant

data class ChangelogSlice(val rootStartTime: Instant, val sliceEndTime: Instant, val sliceRelativeDuration: Duration) {
  constructor(from: Instant, to: Instant) : this(from, to, Duration.ofMillis(to.toEpochMilli() - from.toEpochMilli()))

  val sliceStartTime: Instant = sliceEndTime.minus(sliceRelativeDuration)
  val fullTimeRange: ClosedRange<Instant> = rootStartTime..sliceEndTime
  val sliceChangeRange: ClosedRange<Instant> = sliceStartTime..sliceEndTime

  fun isTimeWithinFullRange(time: Instant) = time in fullTimeRange
  fun isTimeWithinSliceRange(time: Instant) = time in sliceChangeRange

  fun split(): List<ChangelogSlice> {
    val half = sliceRelativeDuration.dividedBy(2)
    val initial = sliceEndTime.minus(sliceRelativeDuration)
    val first = initial.plus(half)
    return listOf(
      ChangelogSlice(rootStartTime, first, half),
      ChangelogSlice(rootStartTime, sliceEndTime, half)
    )
  }

  companion object {
    val none = ChangelogSlice(Instant.MIN, Instant.MIN, Duration.ZERO)
  }
}

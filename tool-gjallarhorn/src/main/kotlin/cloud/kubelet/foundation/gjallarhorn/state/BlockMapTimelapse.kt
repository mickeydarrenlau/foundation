package cloud.kubelet.foundation.gjallarhorn.state

import java.time.Duration
import java.time.Instant
import java.util.stream.Stream

class BlockMapTimelapse<T>(val trim: Pair<BlockCoordinate, BlockCoordinate>? = null) :
  BlockMapRenderPool.RenderPoolDelegate<T> {
  fun calculateChangelogSlices(
    changelog: BlockChangelog, interval: Duration, limit: Int? = null
  ): List<BlockChangelogSlice> {
    val (start, end) = changelog.changeTimeRange
    var intervals = mutableListOf<Instant>()
    var current = start
    while (!current.isAfter(end)) {
      intervals.add(current)
      current = current.plus(interval)
    }

    if (limit != null) {
      intervals = intervals.takeLast(limit).toMutableList()
    }
    return intervals.map { BlockChangelogSlice(start, it, interval) }
  }

  fun splitChangelogSlicesWithThreshold(
    changelog: BlockChangelog,
    targetChangeThreshold: Int,
    minimumTimeInterval: Duration,
    slices: List<BlockChangelogSlice>
  ): List<BlockChangelogSlice> {
    return slices.parallelStream().flatMap { slice ->
      val count = changelog.countRelativeChangesInSlice(slice)
      if (count < targetChangeThreshold ||
        slice.relative < minimumTimeInterval
      ) {
        return@flatMap Stream.of(slice)
      }

      val split = slice.split()
      return@flatMap splitChangelogSlicesWithThreshold(changelog, targetChangeThreshold, minimumTimeInterval, split).parallelStream()
    }.toList()
  }

  override fun buildRenderJobs(
    pool: BlockMapRenderPool<T>,
    trackers: MutableMap<BlockChangelogSlice, BlockLogTracker>
  ) {
    val allBlockOffsets = trackers.map { it.value.calculateZeroBlockOffset() }
    val globalBlockOffset = BlockCoordinate.maxOf(allBlockOffsets)
    val allBlockMaxes = trackers.map { it.value.calculateMaxBlock() }
    val globalBlockMax = BlockCoordinate.maxOf(allBlockMaxes)
    val globalBlockExpanse = BlockExpanse.offsetAndMax(globalBlockOffset, globalBlockMax)

    val renderer = pool.rendererFactory(globalBlockExpanse)
    for ((slice, tracker) in trackers) {
      pool.submitRenderJob(slice) {
        val map = tracker.buildBlockMap(globalBlockExpanse.offset)
        renderer.render(map)
      }
    }
  }

  override fun postProcessTracker(tracker: BlockLogTracker) {
    if (trim != null) {
      tracker.trimOutsideXAndZRange(trim.first, trim.second)
    }
  }
}

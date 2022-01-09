package cloud.kubelet.foundation.gjallarhorn.state

import java.time.Duration
import java.time.Instant

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
    return slices.flatMap { slice ->
      val count = changelog.countRelativeChangesInSlice(slice)
      if (count < targetChangeThreshold ||
        slice.relative < minimumTimeInterval
      ) {
        return@flatMap listOf(slice)
      }

      val split = slice.split()
      return@flatMap splitChangelogSlicesWithThreshold(changelog, targetChangeThreshold, minimumTimeInterval, split)
    }
  }

  override fun buildRenderJobs(
    pool: BlockMapRenderPool<T>,
    trackers: MutableMap<BlockChangelogSlice, BlockLogTracker>
  ) {
    if (trim != null) {
      trackers.values.forEach { tracker ->
        tracker.trimOutsideXAndZRange(trim.first, trim.second)
      }
    }

    for ((slice, tracker) in trackers.entries.toList()) {
      if (tracker.isEmpty()) {
        trackers.remove(slice)
      }
    }

    val allBlockOffsets = trackers.map { it.value.calculateZeroBlockOffset() }
    val globalBlockOffset = BlockCoordinate.maxOf(allBlockOffsets)
    val allBlockMaxes = trackers.map { it.value.calculateMaxBlock() }
    val globalBlockMax = BlockCoordinate.maxOf(allBlockMaxes)
    val globalBlockExpanse = BlockExpanse.offsetAndMax(globalBlockOffset, globalBlockMax)

    val renderer = pool.rendererFactory(globalBlockExpanse)
    for ((slice, tracker) in trackers) {
      if (tracker.isEmpty()) {
        continue
      }

      pool.submitRenderJob(slice) {
        val map = tracker.buildBlockMap(globalBlockExpanse.offset)
        renderer.render(map)
      }
    }
  }
}

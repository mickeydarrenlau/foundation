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
    return intervals.map { it.minus(interval) to it }
  }

  override fun buildRenderJobs(pool: BlockMapRenderPool<T>, trackers: Map<BlockChangelogSlice, BlockLogTracker>) {
    val allBlockOffsets = trackers.map { it.value.calculateZeroBlockOffset() }
    val globalBlockOffset = BlockCoordinate.maxOf(allBlockOffsets)
    val allBlockMaxes = trackers.map { it.value.calculateMaxBlock() }
    val globalBlockMax = BlockCoordinate.maxOf(allBlockMaxes)
    val globalBlockExpanse = BlockExpanse.offsetAndMax(globalBlockOffset, globalBlockMax)

    val renderer = pool.rendererFactory(globalBlockExpanse)
    for ((slice, tracker) in trackers) {
      if (trim != null) {
        tracker.trimOutsideXAndZRange(trim.first, trim.second)
      }

      pool.submitRenderJob(slice) {
        val map = tracker.buildBlockMap(globalBlockExpanse.offset)
        renderer.render(map)
      }
    }
  }
}

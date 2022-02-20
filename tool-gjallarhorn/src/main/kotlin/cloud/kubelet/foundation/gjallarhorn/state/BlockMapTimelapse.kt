package cloud.kubelet.foundation.gjallarhorn.state

class BlockMapTimelapse<T> :
  BlockMapRenderPoolDelegate<T> {
  override fun onSinglePlaybackComplete(pool: BlockMapRenderPool<T>, slice: ChangelogSlice, tracker: BlockLogTracker) {
  }

  override fun onAllPlaybackComplete(
    pool: BlockMapRenderPool<T>,
    trackers: Map<ChangelogSlice, BlockLogTracker>
  ) {
    if (trackers.isEmpty()) {
      return
    }

    val allBlockOffsets = trackers.map { it.value.calculateZeroBlockOffset() }
    val globalBlockOffset = BlockCoordinate.maxOf(allBlockOffsets)
    val allBlockMaxes = trackers.map { it.value.calculateMaxBlock() }
    val globalBlockMax = BlockCoordinate.maxOf(allBlockMaxes)
    val globalBlockExpanse = BlockExpanse.zeroOffsetAndMax(globalBlockOffset, globalBlockMax)

    val renderer = pool.createRendererFunction(globalBlockExpanse)
    for ((slice, tracker) in trackers) {
      pool.submitRenderJob(slice) {
        val map = tracker.buildBlockMap(globalBlockExpanse.offset)
        renderer.render(slice, map)
      }
    }
  }
}

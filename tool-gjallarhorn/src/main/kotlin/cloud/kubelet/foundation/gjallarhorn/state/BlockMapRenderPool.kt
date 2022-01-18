package cloud.kubelet.foundation.gjallarhorn.state

import cloud.kubelet.foundation.gjallarhorn.render.BlockMapRenderer
import org.slf4j.LoggerFactory
import java.util.concurrent.*

class BlockMapRenderPool<T>(
  val changelog: BlockChangelog,
  val blockTrackMode: BlockTrackMode,
  val createRendererFunction: (BlockExpanse) -> BlockMapRenderer<T>,
  val delegate: BlockMapRenderPoolDelegate<T>,
  val threadPoolExecutor: ThreadPoolExecutor,
  val renderResultCallback: (ChangelogSlice, T) -> Unit
) {
  private val trackers = ConcurrentHashMap<ChangelogSlice, BlockLogTracker>()
  private val playbackJobFutures = ConcurrentHashMap<ChangelogSlice, Future<*>>()
  private val renderJobFutures = ConcurrentHashMap<ChangelogSlice, Future<*>>()

  fun submitPlaybackJob(id: String, slice: ChangelogSlice) {
    val future = threadPoolExecutor.submit {
      try {
        runPlaybackSlice(id, slice)
      } catch (e: Exception) {
        logger.error("Failed to run playback job for slice $id", e)
      }
    }
    playbackJobFutures[slice] = future
  }

  fun submitRenderJob(slice: ChangelogSlice, callback: () -> T) {
    val future = threadPoolExecutor.submit {
      try {
        val result = callback()
        renderResultCallback(slice, result)
      } catch (e: Exception) {
        logger.error("Failed to run render job for slice $slice", e)
      }
    }
    renderJobFutures[slice] = future
  }

  fun render(slices: List<ChangelogSlice>) {
    for (slice in slices) {
      submitPlaybackJob((slices.indexOf(slice) + 1).toString(), slice)
    }

    for (future in playbackJobFutures.values) {
      future.get()
    }

    delegate.onAllPlaybackComplete(this, trackers)

    for (future in renderJobFutures.values) {
      future.get()
    }
  }

  private fun runPlaybackSlice(id: String, slice: ChangelogSlice) {
    val start = System.currentTimeMillis()
    val sliced = changelog.slice(slice)
    val tracker = BlockLogTracker(blockTrackMode)
    tracker.replay(sliced)
    if (tracker.isNotEmpty()) {
      trackers[slice] = tracker
      delegate.onSinglePlaybackComplete(this, slice, tracker)
    }
    val end = System.currentTimeMillis()
    val timeInMilliseconds = end - start
    logger.debug("Playback Completed for Slice $id in ${timeInMilliseconds}ms")
  }

  companion object {
    private val logger = LoggerFactory.getLogger(BlockMapRenderPool::class.java)
  }
}

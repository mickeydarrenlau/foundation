package cloud.kubelet.foundation.gjallarhorn.state

import cloud.kubelet.foundation.gjallarhorn.render.BlockMapRenderer
import org.slf4j.LoggerFactory
import java.util.concurrent.*

class BlockMapRenderPool<T>(
  val changelog: BlockChangelog,
  val blockTrackMode: BlockTrackMode,
  val rendererFactory: (BlockExpanse) -> BlockMapRenderer<T>,
  val delegate: RenderPoolDelegate<T>,
  val threadPoolExecutor: ThreadPoolExecutor,
  val renderResultCallback: (BlockChangelogSlice, T) -> Unit
) {
  private val trackers = ConcurrentHashMap<BlockChangelogSlice, BlockLogTracker>()
  private val playbackJobFutures = ConcurrentHashMap<BlockChangelogSlice, Future<*>>()
  private val renderJobFutures = ConcurrentHashMap<BlockChangelogSlice, Future<*>>()

  fun submitPlaybackJob(slice: BlockChangelogSlice) {
    val future = threadPoolExecutor.submit {
      try {
        runPlaybackSlice(slice)
      } catch (e: Exception) {
        logger.error("Failed to run playback job for slice $slice", e)
      }
    }
    playbackJobFutures[slice] = future
  }

  fun submitRenderJob(slice: BlockChangelogSlice, callback: () -> T) {
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

  fun render(slices: List<BlockChangelogSlice>) {
    for (slice in slices) {
      submitPlaybackJob(slice)
    }

    for (future in playbackJobFutures.values) {
      future.get()
    }

    delegate.buildRenderJobs(this, trackers)

    for (future in renderJobFutures.values) {
      future.get()
    }
  }

  private fun runPlaybackSlice(slice: BlockChangelogSlice) {
    val sliced = changelog.slice(slice)
    val tracker = BlockLogTracker(blockTrackMode)
    tracker.replay(sliced)
    if (tracker.isEmpty()) {
      return
    }
    trackers[slice] = tracker
  }

  interface RenderPoolDelegate<T> {
    fun buildRenderJobs(pool: BlockMapRenderPool<T>, trackers: Map<BlockChangelogSlice, BlockLogTracker>)
  }

  companion object {
    private val logger = LoggerFactory.getLogger(BlockMapRenderPool::class.java)
  }
}

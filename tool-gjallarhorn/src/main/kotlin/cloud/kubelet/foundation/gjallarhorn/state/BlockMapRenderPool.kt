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

  fun submitPlaybackJob(id: String, slice: BlockChangelogSlice) {
    val future = threadPoolExecutor.submit {
      try {
        runPlaybackSlice(id, slice)
      } catch (e: Exception) {
        logger.error("Failed to run playback job for slice $id", e)
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
      submitPlaybackJob((slices.indexOf(slice) + 1).toString(), slice)
    }

    for (future in playbackJobFutures.values) {
      future.get()
    }

    delegate.buildRenderJobs(this, trackers)

    for (future in renderJobFutures.values) {
      future.get()
    }
  }

  private fun runPlaybackSlice(id: String, slice: BlockChangelogSlice) {
    val start = System.currentTimeMillis()
    val sliced = changelog.slice(slice)
    val tracker = BlockLogTracker(blockTrackMode)
    tracker.replay(sliced)
    delegate.postProcessTracker(tracker)
    if (tracker.isNotEmpty()) {
      trackers[slice] = tracker
    }
    val end = System.currentTimeMillis()
    val timeInMilliseconds = end - start
    logger.info("Playback Completed for Slice $id in ${timeInMilliseconds}ms")
  }

  interface RenderPoolDelegate<T> {
    fun postProcessTracker(tracker: BlockLogTracker)
    fun buildRenderJobs(pool: BlockMapRenderPool<T>, trackers: MutableMap<BlockChangelogSlice, BlockLogTracker>)
  }

  companion object {
    private val logger = LoggerFactory.getLogger(BlockMapRenderPool::class.java)
  }
}

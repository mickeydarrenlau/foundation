package cloud.kubelet.foundation.gjallarhorn.commands

import cloud.kubelet.foundation.gjallarhorn.render.BlockDiversityRenderer
import cloud.kubelet.foundation.gjallarhorn.render.BlockHeightMapRenderer
import cloud.kubelet.foundation.gjallarhorn.render.BlockImageRenderer
import cloud.kubelet.foundation.gjallarhorn.state.*
import cloud.kubelet.foundation.gjallarhorn.util.compose
import cloud.kubelet.foundation.gjallarhorn.util.savePngFile
import cloud.kubelet.foundation.heimdall.view.BlockChangeView
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.int
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.and
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class BlockLogReplay : CliktCommand("Replay Block Logs", name = "replay-block-log") {
  private val db by requireObject<Database>()
  private val exactTimeAsString by option("--time", help = "Replay Time")
  private val timelapseMode by option("--timelapse", help = "Timelapse Mode").enum<TimelapseMode> { it.id }
  private val timelapseIntervalLimit by option("--timelapse-limit", help = "Timelapse Limit Intervals").int()
  private val render by option("--render", help = "Render Top Down Image").enum<RenderType> { it.id }.required()

  private val considerAirBlocks by option("--consider-air-blocks", help = "Enable Air Block Consideration").flag()

  private val fromCoordinate by option("--trim-from", help = "Trim From Coordinate")
  private val toCoordinate by option("--trim-to", help = "Trim To Coordinate")

  private val logger = LoggerFactory.getLogger(BlockLogReplay::class.java)

  override fun run() {
    if (timelapseMode != null) {
      val changelog = BlockChangelog.query(db)
      val (start, end) = changelog.changeTimeRange
      var intervals = mutableListOf<Instant>()
      var current = start
      while (!current.isAfter(end)) {
        intervals.add(current)
        current = current.plus(timelapseMode!!.interval)
      }

      if (timelapseIntervalLimit != null) {
        intervals = intervals.takeLast(timelapseIntervalLimit!!).toMutableList()
      }

      val trackerPool = ScheduledThreadPoolExecutor(8)
      val trackers = ConcurrentHashMap<Int, BlockLogTracker>()
      for (time in intervals) {
        trackerPool.submit {
          val index = intervals.indexOf(time) + 1
          val tracker =
            buildTrackerState(changelog.slice(time.minus(timelapseMode!!.interval) to time), "Timelapse-${index}")
          if (tracker.isEmpty()) {
            return@submit
          }
          trackers[index] = tracker
        }
      }
      trackerPool.shutdown()
      if (!trackerPool.awaitTermination(12, TimeUnit.HOURS)) {
        throw RuntimeException("Failed to wait for tracker pool.")
      }
      logger.info("State Tracking Completed")
      val allBlockOffsets = trackers.map { it.value.calculateZeroBlockOffset() }
      val globalBlockOffset = BlockCoordinate.maxOf(allBlockOffsets.asSequence())
      val allBlockMaxes = trackers.map { it.value.calculateMaxBlock() }
      val globalBlockMax = BlockCoordinate.maxOf(allBlockMaxes.asSequence())
      val globalBlockExpanse = BlockExpanse.offsetAndMax(globalBlockOffset, globalBlockMax)

      logger.info("Calculations Completed")

      val renderer = render.create(globalBlockExpanse)
      val renderPool = ScheduledThreadPoolExecutor(16)
      val imagePadCount = trackers.size.toString().length
      for ((i, tracker) in trackers.entries) {
        renderPool.submit {
          val suffix = "-${i.toString().padStart(imagePadCount, '0')}"
          saveRenderImage(renderer, tracker, globalBlockExpanse, suffix)
          logger.info("Rendered Timelapse $i")
        }
      }
      renderPool.shutdown()
      if (!renderPool.awaitTermination(12, TimeUnit.HOURS)) {
        throw RuntimeException("Failed to wait for render pool.")
      }
      logger.info("Rendering Completed")
    } else {
      val time = if (exactTimeAsString != null) Instant.parse(exactTimeAsString) else null
      val filter = compose(
        combine = { a, b -> a and b },
        { time != null } to { BlockChangeView.time lessEq time!! }
      )
      val changelog = BlockChangelog.query(db, filter)
      val tracker = buildTrackerState(changelog, "Single-Time")
      val expanse = BlockExpanse.offsetAndMax(tracker.calculateZeroBlockOffset(), tracker.calculateMaxBlock())
      saveRenderImage(render.create(expanse), tracker, expanse)
    }
  }

  fun saveRenderImage(
    renderer: BlockImageRenderer,
    tracker: BlockLogTracker,
    expanse: BlockExpanse,
    suffix: String = ""
  ) {
    val map = tracker.buildBlockMap(expanse.offset)
    val image = renderer.render(map)
    image.savePngFile("${render.id}${suffix}.png")
  }

  fun buildTrackerState(changelog: BlockChangelog, job: String): BlockLogTracker {
    val tracker =
      BlockLogTracker(if (considerAirBlocks) BlockTrackMode.AirOnDelete else BlockTrackMode.RemoveOnDelete)
    tracker.replay(changelog)
    logger.info("Job $job Total Block Changes... ${changelog.changes.size}")
    val uniqueBlockPositions = tracker.blocks.size
    logger.info("Job $job Unique Block Positions... $uniqueBlockPositions")
    maybeTrimState(tracker)
    return tracker
  }

  fun maybeTrimState(tracker: BlockLogTracker) {
    if (fromCoordinate == null || toCoordinate == null) {
      return
    }

    val from = fromCoordinate!!.split(",").map { it.toLong() }
    val to = toCoordinate!!.split(",").map { it.toLong() }

    val fromBlock = BlockCoordinate(from[0], 0, from[1])
    val toBlock = BlockCoordinate(to[0], 0, to[1])

    tracker.trimOutsideXAndZRange(fromBlock, toBlock)
  }

  @Suppress("unused")
  enum class RenderType(
    val id: String,
    val create: (BlockExpanse) -> BlockImageRenderer
  ) {
    BlockDiversity("block-diversity", { expanse -> BlockDiversityRenderer(expanse) }),
    HeightMap("height-map", { expanse -> BlockHeightMapRenderer(expanse) })
  }

  @Suppress("unused")
  enum class TimelapseMode(val id: String, val interval: Duration) {
    ByHour("hours", Duration.ofHours(1)),
    ByDay("days", Duration.ofDays(1)),
    ByFifteenMinutes("fifteen-minutes", Duration.ofMinutes(15))
  }
}

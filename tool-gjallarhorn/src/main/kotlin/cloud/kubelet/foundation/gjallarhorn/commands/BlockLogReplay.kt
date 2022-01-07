package cloud.kubelet.foundation.gjallarhorn.commands

import cloud.kubelet.foundation.gjallarhorn.compose
import cloud.kubelet.foundation.gjallarhorn.render.*
import cloud.kubelet.foundation.gjallarhorn.util.savePngFile
import cloud.kubelet.foundation.heimdall.view.BlockChangeView
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.enum
import jetbrains.exodus.kotlin.notNull
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.awt.image.BufferedImage
import java.time.Duration
import java.time.Instant
import java.util.concurrent.atomic.AtomicLong

class BlockLogReplay : CliktCommand("Replay Block Logs", name = "replay-block-log") {
  private val db by requireObject<Database>()
  private val exactTimeAsString by option("--time", help = "Replay Time")
  private val timeLapseMode by option("--timelapse", help = "Timelapse Mode").enum<TimeLapseMode> { it.id }
  private val render by option("--render", help = "Render Top Down Image").enum<RenderType> { it.id }.required()

  private val considerAirBlocks by option("--consider-air-blocks", help = "Enable Air Block Consideration").flag()

  private val logger = LoggerFactory.getLogger(BlockLogReplay::class.java)

  override fun run() {
    if (timeLapseMode != null) {
      val (start, end) = transaction(db) {
        val minTimeColumn = BlockChangeView.time.min().notNull
        val maxTimeColumn = BlockChangeView.time.max().notNull
        val row = BlockChangeView.slice(minTimeColumn, maxTimeColumn).selectAll().single()
        row[minTimeColumn]!! to row[maxTimeColumn]!!
      }

      val intervals = mutableListOf<Instant>()
      var current = start
      while (!current.isAfter(end)) {
        intervals.add(current)
        current = current.plus(timeLapseMode!!.interval)
      }

      val trackers = mutableMapOf<Int, BlockStateTracker>()
      for (time in intervals) {
        val index = intervals.indexOf(time) + 1
        val tracker = buildTrackerState(time, "Timelapse-${index}")
        if (tracker.isEmpty()) {
          continue
        }
        trackers[index] = tracker
      }

      val allBlockOffsets = trackers.map { it.value.calculateZeroBlockOffset() }
      val globalBlockOffset = BlockPosition.maxOf(allBlockOffsets.asSequence())
      val allBlockMaxes = trackers.map { it.value.calculateZeroBlockOffset() }
      val globalBlockMax = BlockPosition.maxOf(allBlockMaxes.asSequence())
      val globalBlockExpanse = BlockExpanse.offsetAndMax(globalBlockOffset, globalBlockMax)

      for ((i, tracker) in trackers.entries) {
        saveRenderImage(tracker, globalBlockExpanse, "-${i}")
      }
    } else {
      val time = if (exactTimeAsString != null) Instant.parse(exactTimeAsString) else null
      val tracker = buildTrackerState(time, "Single-Time")
      val expanse = BlockExpanse.offsetAndMax(tracker.calculateZeroBlockOffset(), tracker.calculateMaxBlock())
      saveRenderImage(tracker, expanse)
    }
  }

  fun saveRenderImage(tracker: BlockStateTracker, expanse: BlockExpanse, suffix: String = "") {
    val state = BlockStateImage()
    tracker.populateStateImage(state, expanse.offset)
    val image = render.renderBufferedImage(state, expanse)
    image.savePngFile("${render.id}${suffix}.png")
  }

  fun buildTrackerState(time: Instant?, job: String): BlockStateTracker {
    val filter = compose(
      combine = { a, b -> a and b },
      { time != null } to { BlockChangeView.time lessEq time!! }
    )

    val tracker =
      BlockStateTracker(if (considerAirBlocks) BlockTrackMode.AirOnDelete else BlockTrackMode.RemoveOnDelete)

    val blockChangeCounter = AtomicLong()
    transaction(db) {
      BlockChangeView.select(filter).orderBy(BlockChangeView.time).forEach { row ->
        val changeIsBreak = row[BlockChangeView.isBreak]
        val x = row[BlockChangeView.x]
        val y = row[BlockChangeView.y]
        val z = row[BlockChangeView.z]
        val block = row[BlockChangeView.block]

        val location = BlockPosition(x.toLong(), y.toLong(), z.toLong())
        if (changeIsBreak) {
          tracker.delete(location)
        } else {
          tracker.place(location, BlockState(block))
        }

        val count = blockChangeCounter.addAndGet(1)
        if (count % 1000L == 0L) {
          logger.info("Job $job Calculating Block Changes... $count")
        }
      }
    }
    logger.info("Job $job Total Block Changes... ${blockChangeCounter.get()}")

    val uniqueBlockPositions = tracker.blocks.size
    logger.info("Job $job Unique Block Positions... $uniqueBlockPositions")
    return tracker
  }

  enum class RenderType(val id: String, val renderBufferedImage: (BlockStateImage, BlockExpanse) -> BufferedImage) {
    TopDown("top-down", { image, expanse -> image.buildTopDownImage(expanse) }),
    HeightMap("height-map", { image, expanse -> image.buildHeightMapImage(expanse) })
  }

  enum class TimeLapseMode(val id: String, val interval: Duration) {
    ByHour("hours", Duration.ofHours(1)),
    ByDay("days", Duration.ofDays(1))
  }
}

package cloud.kubelet.foundation.gjallarhorn.commands

import cloud.kubelet.foundation.gjallarhorn.render.*
import cloud.kubelet.foundation.gjallarhorn.state.*
import cloud.kubelet.foundation.gjallarhorn.util.compose
import cloud.kubelet.foundation.gjallarhorn.util.savePngFile
import cloud.kubelet.foundation.heimdall.view.BlockChangeView
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.int
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.and
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Font
import java.awt.font.TextLayout
import java.awt.image.BufferedImage
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledThreadPoolExecutor

class BlockChangeTimelapseCommand : CliktCommand("Block Change Timelapse", name = "block-change-timelapse") {
  private val db by requireObject<Database>()
  private val timelapseIntervalLimit by option("--timelapse-limit", help = "Timelapse Limit Intervals").int()
  private val timelapseMode by option("--timelapse", help = "Timelapse Mode").enum<TimelapseMode> { it.id }.required()
  private val timelapseSpeedChangeThreshold by option(
    "--timelapse-change-speed-threshold",
    help = "Timelapse Change Speed Threshold"
  ).int()
  private val timelapseSpeedChangeMinimumIntervalSeconds by option(
    "--timelapse-change-speed-minimum-interval-seconds",
    help = "Timelapse Change Speed Minimum Interval Seconds"
  ).int()

  private val render by option("--render", help = "Render Top Down Image").enum<RenderType> { it.id }.required()

  private val considerAirBlocks by option("--consider-air-blocks", help = "Enable Air Block Consideration").flag()

  private val fromCoordinate by option("--trim-from", help = "Trim From Coordinate")
  private val toCoordinate by option("--trim-to", help = "Trim To Coordinate")

  private val parallelPoolSize by option("--pool-size", help = "Task Pool Size").int().default(8)
  private val inMemoryRender by option("--in-memory-render", help = "Render Images to Memory").flag()
  private val shouldRenderLoop by option("--loop-render", help = "Loop Render").flag()
  private val quadPixelNoop by option("--quad-pixel-noop", help = "Disable Quad Pixel Render").flag()

  private val logger = LoggerFactory.getLogger(BlockChangeTimelapseCommand::class.java)

  override fun run() {
    if (quadPixelNoop) {
      BlockGridRenderer.globalQuadPixelNoop = true
    }
    val threadPoolExecutor = ScheduledThreadPoolExecutor(parallelPoolSize)
    if (shouldRenderLoop) {
      while (true) {
        perform(threadPoolExecutor)
      }
    } else {
      perform(threadPoolExecutor)
    }
    threadPoolExecutor.shutdown()
  }

  private fun perform(threadPoolExecutor: ScheduledThreadPoolExecutor) {
    val trim = maybeBuildTrim()
    val filter = compose(
      combine = { a, b -> a and b },
      { trim?.first?.x != null } to { BlockChangeView.x greaterEq trim!!.first.x },
      { trim?.first?.z != null } to { BlockChangeView.z greaterEq trim!!.first.z },
      { trim?.second?.x != null } to { BlockChangeView.x lessEq trim!!.second.x },
      { trim?.second?.z != null } to { BlockChangeView.z lessEq trim!!.second.z }
    )

    val changelog = BlockChangelog.query(db, filter)
    logger.info("Block Changelog: ${changelog.changes.size} changes")
    val timelapse = BlockMapTimelapse<BufferedImage>()
    var slices = changelog.calculateChangelogSlices(timelapseMode.interval, timelapseIntervalLimit)

    if (timelapseSpeedChangeThreshold != null && timelapseSpeedChangeMinimumIntervalSeconds != null) {
      val minimumInterval = Duration.ofSeconds(timelapseSpeedChangeMinimumIntervalSeconds!!.toLong())
      val blockChangeThreshold = timelapseSpeedChangeThreshold!!

      slices = changelog.splitChangelogSlicesWithThreshold(blockChangeThreshold, minimumInterval, slices)
    }

    logger.info("Timelapse Slices: ${slices.size} slices")

    val imagePadCount = slices.size.toString().length

    val inMemoryPool = if (inMemoryRender) {
      ConcurrentHashMap<ChangelogSlice, BufferedImage>()
    } else {
      null
    }

    val pool = BlockMapRenderPool(
      changelog = changelog,
      blockTrackMode = if (considerAirBlocks) BlockTrackMode.AirOnDelete else BlockTrackMode.RemoveOnDelete,
      delegate = timelapse,
      createRendererFunction = { expanse -> render.create(expanse, db) },
      threadPoolExecutor = threadPoolExecutor
    ) { slice, result ->
      val speed = slice.sliceRelativeDuration.toSeconds().toDouble() / timelapseMode.interval.toSeconds().toDouble()
      val graphics = result.createGraphics()
      val font = Font.decode("Arial Black").deriveFont(24.0f)
      graphics.color = Color.black
      graphics.font = font
      val context = graphics.fontRenderContext
      val text = String.format("%s @ %.4f speed (1 frame = %s sec)", slice.sliceEndTime, speed, slice.sliceRelativeDuration.toSeconds())
      val layout =
        TextLayout(text, font, context)
      layout.draw(graphics, 60f, 60f)
      graphics.dispose()
      val index = slices.indexOf(slice) + 1
      if (inMemoryRender) {
        inMemoryPool?.put(slice, result)
      } else {
        val suffix = "-${index.toString().padStart(imagePadCount, '0')}"
        result.savePngFile("${render.id}${suffix}.png")
      }
      logger.info("Rendered Timelapse Slice $index")
    }

    pool.render(slices)
  }

  private fun maybeBuildTrim(): Pair<BlockCoordinate, BlockCoordinate>? {
    if (fromCoordinate == null || toCoordinate == null) {
      return null
    }

    val from = fromCoordinate!!.split(",").map { it.toLong() }
    val to = toCoordinate!!.split(",").map { it.toLong() }

    val fromBlock = BlockCoordinate(from[0], 0, from[1])
    val toBlock = BlockCoordinate(to[0], 0, to[1])
    return fromBlock to toBlock
  }

  @Suppress("unused")
  enum class RenderType(
    val id: String,
    val create: (BlockExpanse, Database) -> BlockImageRenderer
  ) {
    BlockDiversity("block-diversity", { expanse, _ -> BlockDiversityRenderer(expanse) }),
    HeightMap("height-map", { expanse, _ -> BlockHeightMapRenderer(expanse) }),
    PlayerPosition("player-position", { expanse, db -> PlayerLocationShareRenderer(expanse, db) })
  }

  @Suppress("unused")
  enum class TimelapseMode(val id: String, val interval: Duration) {
    ByHour("hours", Duration.ofHours(1)),
    ByDay("days", Duration.ofDays(1)),
    ByFifteenMinutes("fifteen-minutes", Duration.ofMinutes(15))
  }
}

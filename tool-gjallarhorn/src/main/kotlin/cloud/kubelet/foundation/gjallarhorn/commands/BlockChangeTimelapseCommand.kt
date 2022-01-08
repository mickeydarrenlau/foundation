package cloud.kubelet.foundation.gjallarhorn.commands

import cloud.kubelet.foundation.gjallarhorn.render.BlockDiversityRenderer
import cloud.kubelet.foundation.gjallarhorn.render.BlockHeightMapRenderer
import cloud.kubelet.foundation.gjallarhorn.render.BlockImageRenderer
import cloud.kubelet.foundation.gjallarhorn.state.*
import cloud.kubelet.foundation.gjallarhorn.util.savePngFile
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.int
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory
import java.awt.image.BufferedImage
import java.time.Duration
import java.util.concurrent.ScheduledThreadPoolExecutor

class BlockChangeTimelapseCommand : CliktCommand("Block Change Timelapse", name = "block-change-timelapse") {
  private val db by requireObject<Database>()
  private val timelapseIntervalLimit by option("--timelapse-limit", help = "Timelapse Limit Intervals").int()
  private val timelapseMode by option("--timelapse", help = "Timelapse Mode").enum<TimelapseMode> { it.id }.required()
  private val render by option("--render", help = "Render Top Down Image").enum<RenderType> { it.id }.required()

  private val considerAirBlocks by option("--consider-air-blocks", help = "Enable Air Block Consideration").flag()

  private val fromCoordinate by option("--trim-from", help = "Trim From Coordinate")
  private val toCoordinate by option("--trim-to", help = "Trim To Coordinate")

  private val logger = LoggerFactory.getLogger(BlockChangeTimelapseCommand::class.java)

  override fun run() {
    val threadPoolExecutor = ScheduledThreadPoolExecutor(8)
    val changelog = BlockChangelog.query(db)
    val timelapse = BlockMapTimelapse<BufferedImage>(maybeBuildTrim())
    val slices = timelapse.calculateChangelogSlices(changelog, timelapseMode.interval, timelapseIntervalLimit)
    val imagePadCount = slices.size.toString().length
    val pool = BlockMapRenderPool(
      changelog = changelog,
      blockTrackMode = if (considerAirBlocks) BlockTrackMode.AirOnDelete else BlockTrackMode.RemoveOnDelete,
      delegate = timelapse,
      rendererFactory = { expanse -> render.create(expanse) },
      threadPoolExecutor = threadPoolExecutor
    ) { slice, result ->
      val index = slices.indexOf(slice) + 1
      val suffix = "-${index.toString().padStart(imagePadCount, '0')}"
      result.savePngFile("${render.id}${suffix}.png")
      logger.info("Rendered Timelapse $index")
    }

    pool.render(slices)
    threadPoolExecutor.shutdown()
  }

  fun maybeBuildTrim(): Pair<BlockCoordinate, BlockCoordinate>? {
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

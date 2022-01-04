package cloud.kubelet.foundation.gjallarhorn.commands

import cloud.kubelet.foundation.gjallarhorn.*
import cloud.kubelet.foundation.gjallarhorn.util.savePngFile
import cloud.kubelet.foundation.heimdall.view.BlockChangeView
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.concurrent.atomic.AtomicLong

class BlockLogReplay : CliktCommand("Replay Block Logs", name = "replay-block-log") {
  private val db by requireObject<Database>()
  private val timeAsString by option("--time", help = "Replay Time")
  private val renderTopDown by option("--render-top-down", help = "Render TOp Down Image").flag()
  private val renderHeightMap by option("--render-height-map", help = "Render Height Map Image").flag()

  private val considerAirBlocks by option("--consider-air-blocks", help = "Enable Air Block Consideration").flag()

  override fun run() {
    val filter = compose(
      combine = { a, b -> a and b },
      { timeAsString != null } to { BlockChangeView.time lessEq Instant.parse(timeAsString) }
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
          System.err.println("Calculating Block Changes... $count")
        }
      }
    }
    System.err.println("Total Block Changes... ${blockChangeCounter.get()}")

    val uniqueBlockPositions = tracker.blocks.size
    System.err.println("Unique Block Positions... $uniqueBlockPositions")

    val blockZeroOffset = tracker.calculateZeroBlockOffset()
    System.err.println("Zero Block Offset... $blockZeroOffset")

    if (renderTopDown) {
      val image = BlockStateImage()
      tracker.populate(image, offset = blockZeroOffset)
      val bufferedImage = image.buildTopDownImage()
      bufferedImage.savePngFile("top-down.png")
    } else if (renderHeightMap) {
      val image = BlockStateImage()
      tracker.populate(image, offset = blockZeroOffset)
      val bufferedImage = image.buildHeightMapImage()
      bufferedImage.savePngFile("height-map.png")
    } else {
      println("x,y,z,block")
      for ((position, block) in tracker.blocks) {
        println("${position.x},${position.y},${position.z},${block.type}")
      }
    }
  }
}

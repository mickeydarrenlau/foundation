package cloud.kubelet.foundation.gjallarhorn.commands

import cloud.kubelet.foundation.gjallarhorn.export.ChunkExportLoader
import cloud.kubelet.foundation.gjallarhorn.state.BlockExpanse
import cloud.kubelet.foundation.gjallarhorn.state.BlockLogTracker
import cloud.kubelet.foundation.gjallarhorn.state.ChangelogSlice
import cloud.kubelet.foundation.gjallarhorn.util.savePngFile
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.path
import org.jetbrains.exposed.sql.Database

class ChunkExportLoaderCommand : CliktCommand("Chunk Export Loader", name = "chunk-export-loader") {
  private val db by requireObject<Database>()

  private val exportDirectoryPath by argument("export-directory-path").path()
  private val world by argument("world")
  private val chunkLoadLimit by option("--chunk-limit", help = "Chunk Limit").int()
  private val render by option("--render", help = "Render Top Down Image").enum<ImageRenderType> { it.id }

  override fun run() {
    val tracker = BlockLogTracker(isConcurrent = true)
    val loader = ChunkExportLoader(tracker = tracker)
    loader.loadAllChunksForWorld(exportDirectoryPath, world, fast = true, limit = chunkLoadLimit)
    if (render != null) {
      val expanse = BlockExpanse.zeroOffsetAndMax(tracker.calculateZeroBlockOffset(), tracker.calculateMaxBlock())
      val map = tracker.buildBlockMap(expanse.offset)
      val renderer = render!!.createNewRenderer(expanse, db)
      val image = renderer.render(ChangelogSlice.none, map)
      image.savePngFile("full.png")
    }
  }
}

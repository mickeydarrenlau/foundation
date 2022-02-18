package cloud.kubelet.foundation.gjallarhorn.commands

import cloud.kubelet.foundation.gjallarhorn.export.ChunkExportLoader
import cloud.kubelet.foundation.gjallarhorn.state.BlockExpanse
import cloud.kubelet.foundation.gjallarhorn.state.ChangelogSlice
import cloud.kubelet.foundation.gjallarhorn.state.SparseBlockStateMap
import cloud.kubelet.foundation.gjallarhorn.util.savePngFile
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.path
import org.jetbrains.exposed.sql.Database

class ChunkExportLoaderCommand : CliktCommand("Chunk Export Loader", name = "chunk-export-loader") {
  private val db by requireObject<Database>()

  private val exportDirectoryPath by argument("export-directory-path").path()
  private val world by argument("world")

  private val render by option("--render", help = "Render Top Down Image").enum<ImageRenderType> { it.id }

  override fun run() {
    val map = SparseBlockStateMap()
    val loader = ChunkExportLoader(map)
    loader.loadAllChunksForWorld(exportDirectoryPath, world, fast = true)
    if (render != null) {
      val expanse = BlockExpanse.offsetAndMax(map.calculateZeroBlockOffset(), map.calculateMaxBlock())
      map.applyCoordinateOffset(expanse.offset)
      val renderer = render!!.create(expanse, db)
      val image = renderer.render(ChangelogSlice.none, map)
      image.savePngFile("full.png")
    }
  }
}

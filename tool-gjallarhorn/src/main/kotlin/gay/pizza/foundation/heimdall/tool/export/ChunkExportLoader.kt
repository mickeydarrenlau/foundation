package gay.pizza.foundation.heimdall.tool.export

import gay.pizza.foundation.heimdall.export.ExportedChunk
import gay.pizza.foundation.heimdall.tool.state.BlockCoordinate
import gay.pizza.foundation.heimdall.tool.state.BlockLogTracker
import gay.pizza.foundation.heimdall.tool.state.BlockState
import gay.pizza.foundation.heimdall.tool.state.SparseBlockStateMap
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.util.zip.GZIPInputStream
import kotlin.io.path.inputStream
import kotlin.io.path.listDirectoryEntries

class ChunkExportLoader(
  val map: SparseBlockStateMap? = null,
  val tracker: BlockLogTracker? = null) {
  fun loadAllChunksForWorld(path: Path, world: String, fast: Boolean = false, limit: Int? = null) {
    var chunkFiles = path.listDirectoryEntries("${world}_chunk_*.json.gz")
    if (limit != null) {
      chunkFiles = chunkFiles.take(limit)
    }
    if (fast) {
      chunkFiles.withIndex().toList().parallelStream().forEach { loadChunkFile(it.value, id = it.index) }
    } else {
      for (filePath in chunkFiles) {
        loadChunkFile(filePath, id = chunkFiles.indexOf(filePath))
      }
    }
  }

  fun loadChunkFile(path: Path, id: Int = 0) {
    val fileInputStream = path.inputStream()
    val gzipInputStream = GZIPInputStream(fileInputStream)
    val chunk = Json.decodeFromStream(ExportedChunk.serializer(), gzipInputStream)

    var blockCount = 0L
    val allBlocks = if (tracker != null) mutableMapOf<BlockCoordinate, BlockState>() else null
    for (section in chunk.sections) {
      val x = (chunk.x * 16) + section.x
      val z = (chunk.z * 16) + section.z
      for ((y, bidx) in section.blocks.withIndex()) {
        val block = chunk.blocks[bidx]
        if (block.type == "minecraft:air") {
          continue
        }

        val coordinate = BlockCoordinate(x.toLong(), y.toLong(), z.toLong())
        val state = BlockState.cached(block.type)
        map?.put(coordinate, state)
        if (allBlocks != null) {
          allBlocks[coordinate] = state
        }
        blockCount++
      }
    }

    if (allBlocks != null) {
      tracker?.placeAll(allBlocks)
    }

    logger.info("($id) Chunk X=${chunk.x} Z=${chunk.z} had $blockCount blocks")
  }

  companion object {
    private val logger = LoggerFactory.getLogger(ChunkExportLoader::class.java)
  }
}

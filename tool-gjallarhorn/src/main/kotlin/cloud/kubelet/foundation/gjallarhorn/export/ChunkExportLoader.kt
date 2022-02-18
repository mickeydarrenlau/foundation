package cloud.kubelet.foundation.gjallarhorn.export

import cloud.kubelet.foundation.gjallarhorn.state.BlockCoordinate
import cloud.kubelet.foundation.gjallarhorn.state.BlockState
import cloud.kubelet.foundation.gjallarhorn.state.SparseBlockStateMap
import cloud.kubelet.foundation.heimdall.export.ExportedChunk
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.util.zip.GZIPInputStream
import kotlin.io.path.inputStream
import kotlin.io.path.listDirectoryEntries

class ChunkExportLoader(val map: SparseBlockStateMap) {
  fun loadAllChunksForWorld(path: Path, world: String, fast: Boolean = false) {
    val chunkFiles = path.listDirectoryEntries("${world}_chunk_*.json.gz")
    if (fast) {
      chunkFiles.parallelStream().forEach { loadChunkFile(it, id = chunkFiles.indexOf(it)) }
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
    for (section in chunk.sections) {
      val x = (chunk.x * 16) + section.x
      val z = (chunk.z * 16) + section.z
      for ((y, block) in section.blocks.withIndex()) {
        if (block.type == "minecraft:air") {
          continue
        }

        val coordinate = BlockCoordinate(x.toLong(), y.toLong(), z.toLong())
        val state = BlockState.cached(block.type)
        map.put(coordinate, state)
        blockCount++
      }
    }
    logger.info("($id) Chunk X=${chunk.x} Z=${chunk.z} had $blockCount blocks")
  }

  companion object {
    private val logger = LoggerFactory.getLogger(ChunkExportLoader::class.java)
  }
}

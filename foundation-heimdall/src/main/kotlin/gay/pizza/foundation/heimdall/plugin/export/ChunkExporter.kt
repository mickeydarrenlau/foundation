package gay.pizza.foundation.heimdall.plugin.export

import gay.pizza.foundation.heimdall.export.ExportedBlock
import gay.pizza.foundation.heimdall.export.ExportedChunk
import gay.pizza.foundation.heimdall.export.ExportedChunkSection
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import org.bukkit.Chunk
import org.bukkit.ChunkSnapshot
import org.bukkit.World
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.zip.GZIPOutputStream

class ChunkExporter(private val plugin: Plugin) {
  private val json = Json {
    ignoreUnknownKeys = true
  }

  fun exportLoadedChunksAsync(world: World) {
    exportChunkListAsync(world, world.loadedChunks.toList())
  }

  private fun exportChunkListAsync(world: World, chunks: List<Chunk>) {
    plugin.slF4JLogger.info("Exporting ${chunks.size} chunks")
    val snapshots = chunks.map { it.chunkSnapshot }
    Thread {
      for (snapshot in snapshots) {
        exportChunkSnapshot(world, snapshot)
      }
      plugin.slF4JLogger.info("Exported ${chunks.size} chunks for world ${world.name}")
    }.start()
  }

  private fun exportChunkSnapshot(world: World, snapshot: ChunkSnapshot) {
    val blocks = mutableMapOf<String, Pair<Int, ExportedBlock>>()
    val blockList = mutableListOf<ExportedBlock>()
    val sections = mutableListOf<ExportedChunkSection>()
    val yRange = world.minHeight until world.maxHeight
    val chunkRange = 0..15
    for (x in chunkRange) {
      for (z in chunkRange) {
        sections.add(exportChunkSection(blocks, blockList, snapshot, yRange, x, z))
      }
    }

    val exported = ExportedChunk(blocks = blockList, snapshot.x, snapshot.z, sections)
    saveChunkSnapshot(snapshot, exported)
  }

  private fun saveChunkSnapshot(snapshot: ChunkSnapshot, chunk: ExportedChunk) {
    val file = File("exported_chunks/${snapshot.worldName}_chunk_${snapshot.x}_${snapshot.z}.json.gz")
    if (!file.parentFile.exists()) {
      file.parentFile.mkdirs()
    }

    val fileOutputStream = file.outputStream()
    val gzipOutputStream = GZIPOutputStream(fileOutputStream)
    json.encodeToStream(ExportedChunk.serializer(), chunk, gzipOutputStream)
    gzipOutputStream.close()
  }

  private fun exportChunkSection(
    blocks: MutableMap<String, Pair<Int, ExportedBlock>>,
    blockList: MutableList<ExportedBlock>,
    snapshot: ChunkSnapshot,
    yRange: IntRange,
    x: Int,
    z: Int
  ): ExportedChunkSection {
    val contents = mutableListOf<Int>()
    for (y in yRange) {
      val blockData = snapshot.getBlockData(x, y, z)
      val key = blockData.material.key.toString()
      var idxToBlk = blocks[key]
      if (idxToBlk == null) {
        val idx = blockList.size
        idxToBlk = idx to ExportedBlock(key)
        blockList.add(idxToBlk.second)
        blocks[key] = idxToBlk
      }
      contents.add(idxToBlk.first)
    }
    return ExportedChunkSection(x, z, contents)
  }
}

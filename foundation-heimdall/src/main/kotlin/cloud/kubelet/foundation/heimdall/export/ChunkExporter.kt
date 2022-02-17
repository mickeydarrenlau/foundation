package cloud.kubelet.foundation.heimdall.export

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import org.bukkit.Chunk
import org.bukkit.ChunkSnapshot
import org.bukkit.Server
import org.bukkit.World
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import java.util.zip.GZIPOutputStream

class ChunkExporter(private val plugin: Plugin, private val server: Server, val world: World) {
  private val json = Json {
    ignoreUnknownKeys = true
  }

  fun exportLoadedChunksAsync() {
    exportChunkListAsync(world.loadedChunks.toList())
  }

  private fun exportChunkListAsync(chunks: List<Chunk>) {
    val listOfChunks = chunks.toMutableList()
    doExportChunkList(listOfChunks, AtomicBoolean(false))
  }

  private fun doExportChunkList(chunks: MutableList<Chunk>, check: AtomicBoolean) {
    check.set(false)
    val chunk = chunks.removeFirstOrNull()
    if (chunk == null) {
      plugin.slF4JLogger.info("Chunk Export Complete")
      return
    }

    val snapshot = chunk.chunkSnapshot
    server.scheduler.runTaskAsynchronously(plugin) { ->
      saveChunkSnapshotAndScheduleNext(snapshot, chunks, check)
    }
  }

  private fun saveChunkSnapshotAndScheduleNext(snapshot: ChunkSnapshot, chunks: MutableList<Chunk>, check: AtomicBoolean) {
    exportChunkSnapshot(snapshot)
    if (!check.getAndSet(true)) {
      plugin.server.scheduler.runTask(plugin) { -> doExportChunkList(chunks, check) }
    }
  }

  private fun exportChunkSnapshot(snapshot: ChunkSnapshot) {
    val sections = mutableListOf<ExportedChunkSection>()
    val yRange = world.minHeight until world.maxHeight
    val chunkRange = 0..15
    for (x in chunkRange) {
      for (z in chunkRange) {
        sections.add(exportChunkSection(snapshot, yRange, x, z))
      }
    }

    val exported = ExportedChunk(snapshot.x, snapshot.z, sections)
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

  private fun exportChunkSection(snapshot: ChunkSnapshot, yRange: IntRange, x: Int, z: Int): ExportedChunkSection {
    val blocks = mutableListOf<ExportedBlock>()
    for (y in yRange) {
      val blockData = snapshot.getBlockData(x, y, z)
      val block = ExportedBlock(blockData.material.key.toString())
      blocks.add(block)
    }
    return ExportedChunkSection(x, z, blocks)
  }
}

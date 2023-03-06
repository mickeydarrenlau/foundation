package gay.pizza.foundation.heimdall.plugin.export

import gay.pizza.foundation.heimdall.export.ExportedBlock
import gay.pizza.foundation.heimdall.load.ExportedBlockTable
import gay.pizza.foundation.heimdall.load.WorldLoadFormat
import gay.pizza.foundation.heimdall.load.WorldLoadSimpleWorld
import gay.pizza.foundation.heimdall.load.WorldLoadWorld
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import org.bukkit.World
import java.nio.file.Paths
import kotlin.io.path.outputStream

class WorldLoadExporter {
  private val blockTable = ExportedBlockTable()
  private val worlds = mutableMapOf<String, WorldLoadWorld>()

  fun exportLoadedChunks(world: World) {
    val data = mutableMapOf<Long, MutableMap<Long, MutableMap<Long, Int>>>()
    for (chunk in world.loadedChunks) {
      val snapshot = chunk.chunkSnapshot
      val yRange = world.minHeight until world.maxHeight
      val chunkRange = 0..15
      for (x in chunkRange) {
        for (z in chunkRange) {
          for (y in yRange) {
            val blockInfo = snapshot.getBlockData(x, y, z)
            val block = ExportedBlock(blockInfo.material.key.toString(), blockInfo.asString)
            data.getOrPut(x.toLong()) {
              mutableMapOf()
            }.getOrPut(z.toLong()) {
              mutableMapOf()
            }[y.toLong()] = blockTable.index(block)
          }
        }
      }
    }
    worlds[world.name] = WorldLoadSimpleWorld(world.name, data).compact()
  }

  fun save() {
    val format = WorldLoadFormat(blockTable.blocks, worlds)
    val path = Paths.get("world.load.json")
    path.outputStream().use { stream ->
      Json.encodeToStream(WorldLoadFormat.serializer(), format, stream)
    }
  }
}

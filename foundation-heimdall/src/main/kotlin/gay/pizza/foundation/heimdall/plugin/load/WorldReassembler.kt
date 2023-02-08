package gay.pizza.foundation.heimdall.plugin.load

import gay.pizza.foundation.heimdall.export.ExportedBlock
import gay.pizza.foundation.heimdall.load.WorldLoadFormat
import gay.pizza.foundation.heimdall.load.WorldLoadWorld
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Server
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.concurrent.atomic.AtomicLong

class WorldReassembler(val plugin: Plugin, val server: Server, val format: WorldLoadFormat, val feedback: (String) -> Unit) {
  fun loadInBackground() {
    server.scheduler.runTaskAsynchronously(plugin) { ->
      for (world in server.worlds) {
        val id = world.uid
        var load: WorldLoadWorld? = format.worlds[id.toString().lowercase()]
        if (load == null) {
          load = format.worlds.values.firstOrNull { it.name == world.name }
        }

        if (load == null) {
          feedback("Unable to match world ${world.uid} (${world.name}) to a loadable world, skipping")
          continue
        }

        val blocksToMake = mutableListOf<Pair<Location, ExportedBlock>>()

        for ((x, zBlocks) in load.blocks) {
          for ((z, yBlocks) in zBlocks) {
            for ((y, block) in yBlocks) {
              val material: Material? = Material.matchMaterial(block.type)

              if (material == null) {
                feedback("Unknown Material '${block.type}' at $x $y $z")
                continue
              }

              blocksToMake.add(Location(world, x.toDouble(), y.toDouble(), z.toDouble()) to block)
            }
          }
        }
        blocksToMake.sortBy { it.first.x }

        feedback("Will place ${blocksToMake.size} blocks in ${world.name}")

        val count = AtomicLong()
        var ticks = 0L
        blocksToMake.chunked(1000) { section ->
          val copy = section.toList()
          val runnable = object : BukkitRunnable() {
            override fun run() {
              for ((location, blk) in copy) {
                val block = world.getBlockAt(location)
                val blockData = if (blk.data != null) server.createBlockData(blk.data!!) else null
                if (blockData != null) {
                  block.blockData = blockData
                } else {
                  val material = Material.matchMaterial(blk.type)!!
                  block.type = material
                }
                count.incrementAndGet()
              }
              feedback("Placed ${count.get()} blocks in ${world.name}")
            }
          }
          runnable.runTaskLater(plugin, ticks)
          ticks += 10
        }
      }
    }
  }
}

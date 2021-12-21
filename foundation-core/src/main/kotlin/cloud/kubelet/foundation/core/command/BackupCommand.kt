package cloud.kubelet.foundation.core.command

import cloud.kubelet.foundation.core.FoundationCorePlugin
import cloud.kubelet.foundation.core.Util
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Server
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import java.io.BufferedOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant
import java.util.concurrent.atomic.AtomicBoolean
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class BackupCommand(
  private val plugin: FoundationCorePlugin,
  private val backupPath: Path
) : CommandExecutor {
  override fun onCommand(
    sender: CommandSender, command: Command, label: String, args: Array<String>
  ): Boolean {
    if (!FoundationCorePlugin.BACKUP_ENABLED) {
      sender.sendMessage(
        Component
          .text("Backup is not enabled.")
          .color(TextColor.fromHexString("#FF0000"))
      )
      return true
    }

    if (RUNNING.get()) {
      sender.sendMessage(
        Component
          .text("Backup is already running.")
          .color(TextColor.fromHexString("#FF0000"))
      )
      return true
    }

    try {
      val server = sender.server
      server.scheduler.runTaskAsynchronously(plugin, Runnable {
        runBackup(server)
      })
    } catch (e: Exception) {
      sender.sendMessage(String.format("Failed to backup: %s", e.message))
    }

    return true
  }

  private fun runBackup(server: Server) {
    RUNNING.set(true)

    server.sendMessage(Util.formatSystemMessage("Backup started."))

    val backupFile =
      backupPath.resolve(String.format("backup-%s.zip", Instant.now().toString())).toFile()

    try {
      FileOutputStream(backupFile).use { zipFileStream ->
        ZipOutputStream(BufferedOutputStream(zipFileStream)).use { zipStream ->
          backupPlugins(server, zipStream)
          backupWorlds(server, zipStream)
        }
      }
    } finally {
      RUNNING.set(false)
      server.sendMessage(Util.formatSystemMessage("Backup finished."))
    }
  }

  private fun backupPlugins(server: Server, zipStream: ZipOutputStream) {
    try {
      addDirectoryToZip(zipStream, server.pluginsFolder.toPath())
    } catch (e: IOException) {
      // TODO: Add error handling.
      e.printStackTrace()
    }
  }

  private fun backupWorlds(server: Server, zipStream: ZipOutputStream) {
    val worlds = server.worlds
    for (world in worlds) {
      val worldPath = world.worldFolder.toPath()

      // Save the world.
      server.scheduler.runTask(plugin, Runnable {
        world.save()
      })

      // Disable auto saving to prevent any world corruption while creating a ZIP.
      world.isAutoSave = false
      try {
        addDirectoryToZip(zipStream, worldPath)
      } catch (e: IOException) {
        // TODO: Add error handling.
        e.printStackTrace()
      }

      // Re-enable auto saving for this world.
      world.isAutoSave = true
    }
  }

  private fun addDirectoryToZip(zipStream: ZipOutputStream, directoryPath: Path) {
    val paths = Files.walk(directoryPath)
      .filter { path: Path? -> Files.isRegularFile(path) }
      .toList()
    val buffer = ByteArray(1024)
    val backupsPath = backupPath.toRealPath()

    for (path in paths) {
      val realPath = path.toRealPath()

      if (realPath.startsWith(backupsPath)) {
        plugin.slF4JLogger.info("Skipping file for backup: {}", realPath)
        continue
      }

      FileInputStream(path.toFile()).use { fileStream ->
        val entry = ZipEntry(path.toString())
        zipStream.putNextEntry(entry)

        var n: Int
        while (fileStream.read(buffer).also { n = it } > -1) {
          zipStream.write(buffer, 0, n)
        }
      }
    }
  }

  companion object {
    private val RUNNING = AtomicBoolean()
  }
}
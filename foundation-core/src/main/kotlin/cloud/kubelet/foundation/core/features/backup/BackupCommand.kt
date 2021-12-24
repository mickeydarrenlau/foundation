package cloud.kubelet.foundation.core.features.backup

import cloud.kubelet.foundation.core.FoundationCorePlugin
import cloud.kubelet.foundation.core.Util
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Server
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
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

// TODO: Clean up dependency injection.
class BackupCommand(
  private val plugin: FoundationCorePlugin,
  private val backupsPath: Path,
  private val config: BackupConfig,
  private val s3Client: S3Client,
) : CommandExecutor {
  override fun onCommand(
    sender: CommandSender, command: Command, label: String, args: Array<String>
  ): Boolean {
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

  // TODO: Pull backup creation code into a separate service.
  private fun runBackup(server: Server) = try {
    RUNNING.set(true)

    server.sendMessage(Util.formatSystemMessage("Backup started."))

    val backupFileName = String.format("backup-%s.zip", Instant.now().toString())
    val backupPath = backupsPath.resolve(backupFileName)
    val backupFile = backupPath.toFile()

    FileOutputStream(backupFile).use { zipFileStream ->
      ZipOutputStream(BufferedOutputStream(zipFileStream)).use { zipStream ->
        backupPlugins(server, zipStream)
        backupWorlds(server, zipStream)
      }
    }

    // TODO: Pull upload code out into a separate service.
    if (config.s3.accessKeyId.isNotEmpty()) {
      s3Client.putObject(
        PutObjectRequest.builder().apply {
          bucket(config.s3.bucket)
          key("${config.s3.baseDirectory}/$backupFileName")
        }.build(),
        backupPath
      )
    }
    Unit
  } finally {
    RUNNING.set(false)
    server.sendMessage(Util.formatSystemMessage("Backup finished."))
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

      // Save the world, must be run on the main thread.
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
    val backupsPath = backupsPath.toRealPath()

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
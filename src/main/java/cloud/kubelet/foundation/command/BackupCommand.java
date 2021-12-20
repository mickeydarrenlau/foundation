package cloud.kubelet.foundation.command;

import cloud.kubelet.foundation.Foundation;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BackupCommand implements CommandExecutor {

  private static final AtomicBoolean RUNNING = new AtomicBoolean();
  private final Path backupPath;

  public BackupCommand(Path backupPath) {
    this.backupPath = backupPath;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
      @NotNull String label, @NotNull String[] args) {
    if (!Foundation.BACKUP_ENABLED) {
      sender.sendMessage(
          Component
              .text("Backup is not enabled.")
              .color(TextColor.fromHexString("#FF0000"))
      );
      return true;
    }
    if (RUNNING.get()) {
      sender.sendMessage(
          Component
              .text("Backup is already running.")
              .color(TextColor.fromHexString("#FF0000"))
      );
    } else {
      try {
        runBackup(sender);
      } catch (Exception e) {
        sender.sendMessage(String.format("Failed to backup: %s", e.getMessage()));
      }
    }

    return true;
  }

  private void runBackup(CommandSender sender) throws IOException {
    RUNNING.set(true);

    final var backupFile = backupPath.resolve(
        String.format("backup-%s.zip", Instant.now().toString())).toFile();
    final var zipFileStream = new FileOutputStream(backupFile);

    try (zipFileStream; var zipStream = new ZipOutputStream(
        new BufferedOutputStream(zipFileStream))) {
      final var worlds = sender.getServer().getWorlds();
      for (World world : worlds) {
        final var name = world.getName();
        final var worldPath = world.getWorldFolder().toPath().toString();
        sender.sendMessage(String.format("%s: %s", name, worldPath));

        world.save();
        world.setAutoSave(false);

        try {
          final var paths = Files.walk(Paths.get(worldPath))
              .filter(Files::isRegularFile)
              .toList();

          for (Path path : paths) {
            try (InputStream fileStream = new FileInputStream(path.toFile())) {
              final var entry = new ZipEntry(path.toString());
              zipStream.putNextEntry(entry);
              int n;
              byte[] buffer = new byte[1024];
              while ((n = fileStream.read(buffer)) > -1) {
                zipStream.write(buffer, 0, n);
              }
            }
          }
        } catch (IOException e) {
          e.printStackTrace();
        }

        world.setAutoSave(true);
      }
    } finally {
      RUNNING.set(false);
    }
  }
}

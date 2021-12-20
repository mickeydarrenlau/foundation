package cloud.kubelet.foundation;

import cloud.kubelet.foundation.command.BackupCommand;
import io.papermc.paper.event.player.ChatEvent;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Foundation extends JavaPlugin implements Listener {
  public static final boolean BACKUP_ENABLED = true;
  private static final String BACKUPS_DIRECTORY = "backups";

  @Override
  public void onEnable() {
    Path dataPath = getDataFolder().toPath();
    Path backupPath = dataPath.resolve(BACKUPS_DIRECTORY);

    // Create Foundation plugin directories.
    dataPath.toFile().mkdir();
    backupPath.toFile().mkdir();

    // Register this as an event listener.
    getServer().getPluginManager().registerEvents(this, this);

    // Set up commands.
    Objects.requireNonNull(getCommand("fbackup")).setExecutor(new BackupCommand(backupPath));

    final var log = getSLF4JLogger();
    log.info("Features:");
    Util.printFeatureStatus(log, "Backup: ", BACKUP_ENABLED);
  }

  @Override
  public void onDisable() {
  }

  private final Component leftBracket = Component.text('[');
  private final Component rightBracket = Component.text(']');

  @EventHandler
  private void onChatMessage(ChatEvent e) {
    e.setCancelled(true);

    final var name = e.getPlayer().displayName();
    final var component = Component.empty()
        .append(leftBracket)
        .append(name)
        .append(rightBracket)
        .append(Component.text(' '))
        .append(e.message());

    getServer().sendMessage(component);
  }
}

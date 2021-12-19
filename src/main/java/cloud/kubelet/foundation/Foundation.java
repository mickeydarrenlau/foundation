package cloud.kubelet.foundation;

import io.papermc.paper.event.player.ChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Foundation extends JavaPlugin implements Listener {

  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(this, this);

    getDataFolder().mkdir();
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

package cloud.kubelet.foundation.bifrost

import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

interface EventHandler {
  fun onPlayerJoin(e: PlayerJoinEvent)
  fun onPlayerQuit(e: PlayerQuitEvent)
  fun onChat(e: AsyncChatEvent)
}

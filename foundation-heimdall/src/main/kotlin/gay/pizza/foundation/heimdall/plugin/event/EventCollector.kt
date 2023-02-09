package gay.pizza.foundation.heimdall.plugin.event

import org.bukkit.Server
import org.bukkit.event.Listener

interface EventCollector<T : HeimdallEvent> : Listener {
  fun beforeBufferFlush() {}
  fun onPluginDisable(server: Server) {}
}

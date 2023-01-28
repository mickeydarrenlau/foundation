package gay.pizza.foundation.chaos.modules

import org.bukkit.event.Listener

interface ChaosModule : Listener {
  fun id(): String
  fun what(): String
  fun load() {}
  fun unload() {}
}

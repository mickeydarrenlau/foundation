package gay.pizza.foundation.chaos.modules

import org.bukkit.event.Listener

interface ChaosModule : Listener {
  fun id(): String
  fun name(): String
  fun what(): String
  fun activate() {}
  fun deactivate() {}
}

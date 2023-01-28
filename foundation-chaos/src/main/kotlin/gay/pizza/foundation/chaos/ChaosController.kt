package gay.pizza.foundation.chaos

import gay.pizza.foundation.chaos.model.ChaosConfig
import gay.pizza.foundation.chaos.modules.ChaosModule
import gay.pizza.foundation.chaos.modules.ChaosModules
import net.kyori.adventure.text.Component
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import java.util.concurrent.atomic.AtomicBoolean

class ChaosController(val plugin: Plugin, val config: ChaosConfig) : Listener {
  val state: AtomicBoolean = AtomicBoolean(false)
  val selectorController = ChaosSelectorController(this, plugin)

  val allModules = ChaosModules.all(plugin)
  private var allowedModules: List<ChaosModule> = emptyList()
  private var activeModules = mutableSetOf<ChaosModule>()

  fun load() {
    if (state.get()) {
      return
    }
    allowedModules = allModules.filter { config.enable[it.id()] ?: true }
    state.set(true)
    selectorController.schedule()
  }

  fun activateAll() {
    for (module in allowedModules) {
      if (activeModules.contains(module)) {
        continue
      }
      activate(module)
    }
  }

  fun activate(module: ChaosModule) {
    plugin.server.pluginManager.registerEvents(module, plugin)
    module.activate()
    activeModules.add(module)
    plugin.server.broadcast(Component.text("Chaos Module Activated: ${module.id()}"))
  }

  fun deactivate(module: ChaosModule) {
    HandlerList.unregisterAll(module)
    module.deactivate()
    activeModules.remove(module)
    plugin.server.broadcast(Component.text("Chaos Module Deactivated: ${module.id()}"))
  }

  fun deactivateAll() {
    for (module in activeModules.toList()) {
      deactivate(module)
    }
  }

  fun unload() {
    if (!state.get()) {
      return
    }
    deactivateAll()
    state.set(false)
    selectorController.cancel()
  }
}

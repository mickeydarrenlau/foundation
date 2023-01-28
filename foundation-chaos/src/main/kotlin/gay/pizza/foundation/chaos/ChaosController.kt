package gay.pizza.foundation.chaos

import gay.pizza.foundation.chaos.model.ChaosConfig
import gay.pizza.foundation.chaos.modules.ChaosModule
import gay.pizza.foundation.chaos.modules.ChaosModules
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import java.util.concurrent.atomic.AtomicBoolean

class ChaosController(private val plugin: Plugin, val config: ChaosConfig) : Listener {
  val state: AtomicBoolean = AtomicBoolean(false)

  private val allModules = ChaosModules.all(plugin)
  private var modules: List<ChaosModule> = emptyList()

  fun load() {
    if (state.get()) {
      return
    }
    modules = allModules.filter { config.enable[it.id()] ?: true }
    modules.forEach { module ->
      plugin.server.pluginManager.registerEvents(module, plugin)
      module.load()
    }
    state.set(true)
  }

  fun unload() {
    if (!state.get()) {
      return
    }
    modules.forEach { module ->
      HandlerList.unregisterAll(module)
      module.unload()
    }
    state.set(false)
  }
}

package gay.pizza.foundation.chaos

import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask

class ChaosSelectorController(val controller: ChaosController, val plugin: Plugin) {
  var task: BukkitTask? = null

  fun schedule() {
    cancel()
    task = plugin.server.scheduler.runTaskTimer(controller.plugin, { ->
      select()
    }, controller.config.selectionTimerTicks, controller.config.selectionTimerTicks)
  }

  fun select() {
    controller.deactivateAll()
    val module = controller.allModules.random()
    controller.activate(module)
  }

  fun cancel() {
    task?.cancel()
  }
}

package cloud.kubelet.foundation.core.abstraction

import cloud.kubelet.foundation.core.FoundationCorePlugin
import org.bukkit.command.CommandExecutor
import org.bukkit.command.TabCompleter
import org.bukkit.event.Listener
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module

abstract class Feature : KoinComponent, Listener {
  private val plugin by inject<FoundationCorePlugin>()

  open fun enable() {}
  open fun disable() {}
  open fun module() = module {}

  protected fun registerCommandExecutor(name: String, executor: CommandExecutor) {
    registerCommandExecutor(listOf(name), executor)
  }

  protected fun registerCommandExecutor(names: List<String>, executor: CommandExecutor) {
    for (name in names) {
      val command = plugin.getCommand(name) ?: throw Exception("Failed to get $name command")
      command.setExecutor(executor)
      if (executor is TabCompleter) {
        command.tabCompleter = executor
      }
    }
  }
}

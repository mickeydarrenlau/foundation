package cloud.kubelet.foundation.core.command

import cloud.kubelet.foundation.core.FoundationCorePlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class StoreStatsCommand(private val plugin: FoundationCorePlugin) : CommandExecutor {
  override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
    plugin.persistentStores.forEach { (name, store) ->
      store.transact { tx ->
        val types = tx.entityTypes
        val counts = types.associateWith { type -> tx.getAll(type).size() }.toSortedMap()
        sender.sendMessage("Store $name ->", *counts.map { "  ${it.key} -> ${it.value} entries" }.toTypedArray())
      }
    }
    return true
  }
}

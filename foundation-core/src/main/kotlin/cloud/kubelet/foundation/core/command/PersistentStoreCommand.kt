package cloud.kubelet.foundation.core.command

import cloud.kubelet.foundation.core.FoundationCorePlugin
import jetbrains.exodus.entitystore.Entity
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import java.util.*

class PersistentStoreCommand(private val plugin: FoundationCorePlugin) : CommandExecutor {
  override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
    if (args.isEmpty()) {
      sender.sendMessage("Invalid Command Usage.")
      return true
    }

    when (args[0]) {
      "stats" -> {
        plugin.persistentStores.forEach { (name, store) ->
          val counts = store.transact {
            entityTypes.associateWith { type -> getAll(type).size() }.toSortedMap()
          }

          sender.sendMessage(
            "Store $name ->",
            *counts.map { "  ${it.key} -> ${it.value} entries" }.toTypedArray()
          )
        }
      }
      "sample" -> {
        if (args.size != 3) {
          sender.sendMessage("Invalid Subcommand Usage.")
          return true
        }

        val storeName = args[1]
        val entityTypeName = args[2]
        val store = plugin.getPersistentStore(storeName)
        val random = Random()
        store.transact {
          val entities = getAll(entityTypeName)
          val results = mutableListOf<Entity>()
          for (entity in entities) {
            if (random.nextBoolean()) {
              results.add(entity)
            }

            if (results.size == 3) {
              break
            }
          }

          for (result in results) {
            sender.sendMessage(
              "Entity ${result.id.localId} ->",
              *result.propertyNames.map { "  ${it}: ${result.getProperty(it)}" }.toTypedArray()
            )
          }
        }
      }
      else -> {
        sender.sendMessage("Unknown Subcommand.")
      }
    }
    return true
  }
}

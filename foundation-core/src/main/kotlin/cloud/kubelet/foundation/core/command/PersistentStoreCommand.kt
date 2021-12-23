package cloud.kubelet.foundation.core.command

import cloud.kubelet.foundation.core.FoundationCorePlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class PersistentStoreCommand(private val plugin: FoundationCorePlugin) : CommandExecutor, TabCompleter {
  private val allSubCommands = mutableListOf("stats", "sample", "delete-all-entities")

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
        store.transact {
          val entities = getAll(entityTypeName).take(3)
          for (entity in entities) {
            sender.sendMessage(
              "Entity ${entity.id.localId} ->",
              *entity.propertyNames.map { "  ${it}: ${entity.getProperty(it)}" }.toTypedArray()
            )
          }
        }
      }
      "delete-all-entities" -> {
        if (args.size != 3) {
          sender.sendMessage("Invalid Subcommand Usage.")
          return true
        }

        val storeName = args[1]
        val entityTypeName = args[2]
        val store = plugin.getPersistentStore(storeName)
        store.transact {
          store.deleteAllEntities(entityTypeName)
        }
        sender.sendMessage("Deleted all entities for $storeName $entityTypeName")
      }
      else -> {
        sender.sendMessage("Unknown Subcommand.")
      }
    }
    return true
  }

  override fun onTabComplete(
    sender: CommandSender,
    command: Command,
    alias: String,
    args: Array<out String>
  ): MutableList<String> = when {
    args.isEmpty() -> {
      allSubCommands
    }
    args.size == 1 -> {
      allSubCommands.filter { it.startsWith(args[0]) }.toMutableList()
    }
    else -> {
      mutableListOf()
    }
  }
}

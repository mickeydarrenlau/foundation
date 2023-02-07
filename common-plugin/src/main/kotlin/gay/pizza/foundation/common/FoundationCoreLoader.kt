package gay.pizza.foundation.common

import gay.pizza.foundation.shared.IFoundationCore
import org.bukkit.Server

object FoundationCoreLoader {
  fun get(server: Server): IFoundationCore {
    return server.pluginManager.getPlugin("Foundation") as IFoundationCore?
      ?: throw RuntimeException("Foundation Core is not loaded!")
  }
}

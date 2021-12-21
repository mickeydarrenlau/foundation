package cloud.kubelet.foundation.bifrost

import cloud.kubelet.foundation.core.FoundationCorePlugin
import org.bukkit.plugin.java.JavaPlugin

class FoundationBifrostPlugin : JavaPlugin() {
  override fun onEnable() {
    slF4JLogger.info("Enabling!")

    val foundation = server.pluginManager.getPlugin("Foundation") as FoundationCorePlugin
    slF4JLogger.info("Plugin data path: ${foundation.pluginDataPath}")
  }
}

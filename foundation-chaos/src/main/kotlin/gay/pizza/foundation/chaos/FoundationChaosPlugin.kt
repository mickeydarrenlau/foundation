package gay.pizza.foundation.chaos

import com.charleskorn.kaml.Yaml
import gay.pizza.foundation.chaos.model.ChaosConfig
import gay.pizza.foundation.core.FoundationCorePlugin
import gay.pizza.foundation.core.Util
import org.bukkit.plugin.java.JavaPlugin
import kotlin.io.path.inputStream

class FoundationChaosPlugin : JavaPlugin() {
  lateinit var config: ChaosConfig

  val controller by lazy {
    ChaosController(this, config)
  }

  override fun onEnable() {
    val foundation = server.pluginManager.getPlugin("Foundation") as FoundationCorePlugin
    slF4JLogger.info("Plugin data path: ${foundation.pluginDataPath}")
    val configPath = Util.copyDefaultConfig<FoundationChaosPlugin>(
      slF4JLogger,
      foundation.pluginDataPath,
      "chaos.yaml"
    )
    config = Yaml.default.decodeFromStream(ChaosConfig.serializer(), configPath.inputStream())
    val chaosCommand = getCommand("chaos")!!
    chaosCommand.setExecutor(ChaosToggleCommand())
  }
}

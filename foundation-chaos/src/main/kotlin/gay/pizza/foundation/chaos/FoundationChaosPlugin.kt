package gay.pizza.foundation.chaos

import com.charleskorn.kaml.Yaml
import gay.pizza.foundation.chaos.model.ChaosConfig
import gay.pizza.foundation.common.FoundationCoreLoader
import gay.pizza.foundation.shared.PluginMainClass
import gay.pizza.foundation.shared.copyDefaultConfig
import org.bukkit.plugin.java.JavaPlugin
import kotlin.io.path.inputStream

@PluginMainClass
class FoundationChaosPlugin : JavaPlugin() {
  lateinit var config: ChaosConfig

  val controller by lazy {
    ChaosController(this, config)
  }

  override fun onEnable() {
    val foundation = FoundationCoreLoader.get(server)
    val configPath = copyDefaultConfig<FoundationChaosPlugin>(
      slF4JLogger,
      foundation.pluginDataPath,
      "chaos.yaml"
    )
    config = Yaml.default.decodeFromStream(ChaosConfig.serializer(), configPath.inputStream())
    val chaosCommand = getCommand("chaos")!!
    chaosCommand.setExecutor(ChaosToggleCommand())
  }
}

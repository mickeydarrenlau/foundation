package gay.pizza.foundation.tailscale

import com.charleskorn.kaml.Yaml
import gay.pizza.foundation.common.BaseFoundationPlugin
import gay.pizza.foundation.common.FoundationCoreLoader
import gay.pizza.foundation.shared.PluginMainClass
import gay.pizza.foundation.shared.copyDefaultConfig
import kotlin.io.path.inputStream

@PluginMainClass
class FoundationTailscalePlugin : BaseFoundationPlugin() {
  lateinit var config: TailscaleConfig
  lateinit var controller: TailscaleController

  override fun onEnable() {
    val foundation = FoundationCoreLoader.get(server)
    val configPath = copyDefaultConfig<FoundationTailscalePlugin>(
      slF4JLogger,
      foundation.pluginDataPath,
      "tailscale.yaml"
    )
    config = Yaml.default.decodeFromStream(TailscaleConfig.serializer(), configPath.inputStream())
    controller = TailscaleController(server, config)
    controller.enable()
  }

  override fun onDisable() {
    controller.disable()
  }
}

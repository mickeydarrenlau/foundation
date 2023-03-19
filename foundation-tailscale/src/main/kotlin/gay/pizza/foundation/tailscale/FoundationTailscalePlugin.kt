package gay.pizza.foundation.tailscale

import gay.pizza.foundation.common.BaseFoundationPlugin
import gay.pizza.foundation.common.FoundationCoreLoader
import gay.pizza.foundation.shared.PluginMainClass

@PluginMainClass
class FoundationTailscalePlugin : BaseFoundationPlugin() {
  lateinit var config: TailscaleConfig
  lateinit var controller: TailscaleController

  override fun onEnable() {
    val foundation = FoundationCoreLoader.get(server)
    config = loadConfigurationWithDefault(
      foundation,
      TailscaleConfig.serializer(),
      "tailscale.yaml"
    )
    controller = TailscaleController(server, config)
    controller.enable()
  }

  override fun onDisable() {
    controller.disable()
  }
}

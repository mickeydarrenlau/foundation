package gay.pizza.foundation.chaos

import gay.pizza.foundation.chaos.model.ChaosConfig
import gay.pizza.foundation.common.BaseFoundationPlugin
import gay.pizza.foundation.common.FoundationCoreLoader
import gay.pizza.foundation.shared.PluginMainClass

@PluginMainClass
class FoundationChaosPlugin : BaseFoundationPlugin() {
  lateinit var config: ChaosConfig

  val controller by lazy {
    ChaosController(this, config)
  }

  override fun onEnable() {
    val foundation = FoundationCoreLoader.get(server)
    config = loadConfigurationWithDefault(
      foundation,
      ChaosConfig.serializer(),
      "heimdall.yaml"
    )
    registerCommandExecutor("chaos", ChaosToggleCommand())
  }
}

package gay.pizza.foundation.chaos.model

import kotlinx.serialization.Serializable

@Serializable
class ChaosConfig(
  val allowed: Boolean = true,
  val defaultModuleConfiguration: ChaosModuleConfig,
  val modules: Map<String, ChaosModuleConfig> = emptyMap(),
  val selection: ChaosSelectionConfig
)

@Serializable
class ChaosModuleConfig(
  val enabled: Boolean
)

@Serializable
class ChaosSelectionConfig(
  val timerTicks: Long
)

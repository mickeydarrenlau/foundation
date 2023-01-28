package gay.pizza.foundation.chaos.model

import kotlinx.serialization.Serializable

@Serializable
class ChaosConfig(
  val allowed: Boolean = true,
  val enable: Map<String, Boolean> = mapOf(),
  val selectionTimerTicks: Long
)

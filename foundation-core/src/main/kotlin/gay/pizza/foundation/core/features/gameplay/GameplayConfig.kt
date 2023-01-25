package gay.pizza.foundation.core.features.gameplay

import kotlinx.serialization.Serializable

@Serializable
data class GameplayConfig(
  val mobs: MobsConfig = MobsConfig(),
)

@Serializable
data class MobsConfig(
  val disableEndermanGriefing: Boolean = false,
  val disableFreezeDamage: Boolean = false,
  val allowLeads: Boolean = false,
)

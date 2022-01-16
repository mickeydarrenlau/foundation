package cloud.kubelet.foundation.core.features.gameplay

import kotlinx.serialization.Serializable

@Serializable
data class GameplayConfig(
  val mobs: MobsConfig,
)

@Serializable
data class MobsConfig(
  val disableEndermanGriefing: Boolean,
  val disableFreezeDamage: Boolean,
  val allowLeads: Boolean,
)

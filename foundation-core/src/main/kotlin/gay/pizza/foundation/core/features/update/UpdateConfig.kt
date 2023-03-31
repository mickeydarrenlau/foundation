package gay.pizza.foundation.core.features.update

import kotlinx.serialization.Serializable

@Serializable
data class UpdateConfig(
  val autoUpdateSchedule: AutoUpdateSchedule
)

@Serializable
class AutoUpdateSchedule(
  val cron: String = ""
)

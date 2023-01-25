package gay.pizza.foundation.core.features.dev

import kotlinx.serialization.Serializable

@Serializable
class DevUpdateConfig(
  val port: Int = 8484,
  val token: String,
  val ipAllowList: List<String> = listOf("*")
)

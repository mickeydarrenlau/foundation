package gay.pizza.foundation.heimdall.plugin.model

import kotlinx.serialization.Serializable

@Serializable
data class HeimdallConfig(
  val enabled: Boolean = false,
  val db: DbConfig,
  val blockChangePrecise: Boolean = false,
  val blockChangePreciseImmediate: Boolean = false
)

@Serializable
data class DbConfig(
  val url: String,
  val username: String,
  val password: String
)

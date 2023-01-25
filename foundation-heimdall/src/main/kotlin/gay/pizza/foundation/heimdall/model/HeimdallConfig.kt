package gay.pizza.foundation.heimdall.model

import kotlinx.serialization.Serializable

@Serializable
data class HeimdallConfig(
  val enabled: Boolean = false,
  val db: DbConfig
)

@Serializable
data class DbConfig(
  val url: String,
  val username: String,
  val password: String
)

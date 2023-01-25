package gay.pizza.foundation.bifrost.model

import kotlinx.serialization.Serializable

@Serializable
data class BifrostConfig(
  val authentication: BifrostAuthentication,
  val channel: BifrostChannel,
  val enableDebugLog: Boolean = false
)

@Serializable
data class BifrostAuthentication(
  val token: String,
)

@Serializable
data class BifrostChannel(
  val id: String,
  val bridge: Boolean = true,
  val sendStart: Boolean = true,
  val sendShutdown: Boolean = true,
  val sendPlayerJoin: Boolean = true,
  val sendPlayerQuit: Boolean = true,
  val sendPlayerDeath: Boolean = true,
  val sendPlayerAdvancement: Boolean = true
)

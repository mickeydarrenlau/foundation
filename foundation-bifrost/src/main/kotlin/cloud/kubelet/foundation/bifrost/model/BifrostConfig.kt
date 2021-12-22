package cloud.kubelet.foundation.bifrost.model

import kotlinx.serialization.Serializable

@Serializable
data class BifrostConfig(
  val authentication: BifrostAuthentication,
  val channel: BifrostChannel,
)

@Serializable
data class BifrostAuthentication(
  val token: String,
)

@Serializable
data class BifrostChannel(
  val id: String,
)

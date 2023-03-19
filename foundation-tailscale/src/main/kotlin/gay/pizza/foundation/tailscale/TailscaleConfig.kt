package gay.pizza.foundation.tailscale

import kotlinx.serialization.Serializable

@Serializable
data class TailscaleConfig(
  val enabled: Boolean = false,
  val hostname: String,
  val controlUrl: String? = null,
  val authKey: String? = null,
  val tailscalePath: String? = null,
  val ephemeral: Boolean = false
)

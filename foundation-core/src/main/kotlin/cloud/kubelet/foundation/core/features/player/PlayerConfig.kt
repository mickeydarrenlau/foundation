package cloud.kubelet.foundation.core.features.player

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerConfig(
  @SerialName("anti-idle")
  val antiIdle: AntiIdleConfig = AntiIdleConfig(),
)

@Serializable
data class AntiIdleConfig(
  val enabled: Boolean = false,
  val idleDuration: Int = 3600,
  val ignore: List<String> = listOf(),
)

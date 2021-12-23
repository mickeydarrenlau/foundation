package cloud.kubelet.foundation.core.features.update

import kotlinx.serialization.Serializable

@Serializable
data class ModuleManifest(
  val version: String,
  val artifacts: List<String>,
)

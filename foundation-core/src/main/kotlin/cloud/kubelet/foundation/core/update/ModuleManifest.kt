package cloud.kubelet.foundation.core.update

import kotlinx.serialization.Serializable

@Serializable
data class ModuleManifest(
  val version: String,
  val artifacts: List<String>,
)

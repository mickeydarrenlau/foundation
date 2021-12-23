package cloud.kubelet.foundation.core.features.dev

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
class DevUpdatePayload(
  @SerialName("object_kind")
  val objectKind: String,
  @SerialName("object_attributes")
  val objectAttributes: Map<String, JsonElement>
)

package cloud.kubelet.foundation.gjallarhorn.state

import kotlinx.serialization.Serializable

@Serializable
data class BlockState(val type: String)

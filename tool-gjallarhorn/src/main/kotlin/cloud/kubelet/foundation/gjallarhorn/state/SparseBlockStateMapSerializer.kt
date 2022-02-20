package cloud.kubelet.foundation.gjallarhorn.state

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class SparseBlockStateMapSerializer : KSerializer<SparseBlockStateMap> {
  private val internal = MapSerializer(Long.serializer(), MapSerializer(Long.serializer(), MapSerializer(Long.serializer(), BlockState.serializer())))
  override val descriptor: SerialDescriptor
    get() = internal.descriptor

  override fun deserialize(decoder: Decoder): SparseBlockStateMap {
    val data = internal.deserialize(decoder)
    return SparseBlockStateMap(data)
  }

  override fun serialize(encoder: Encoder, value: SparseBlockStateMap) {
    internal.serialize(encoder, value.blocks)
  }
}

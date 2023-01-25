package gay.pizza.foundation.gjallarhorn.state

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class BlockStateSerializer : KSerializer<BlockState> {
  override val descriptor: SerialDescriptor
    get() = String.serializer().descriptor

  override fun deserialize(decoder: Decoder): BlockState {
    return BlockState.cached(decoder.decodeString())
  }

  override fun serialize(encoder: Encoder, value: BlockState) {
    encoder.encodeString(value.type)
  }
}

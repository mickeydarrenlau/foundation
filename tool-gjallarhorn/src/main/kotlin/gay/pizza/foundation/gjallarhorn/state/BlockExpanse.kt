package gay.pizza.foundation.gjallarhorn.state

import kotlinx.serialization.Serializable

@Serializable
data class BlockExpanse(
  val offset: BlockCoordinate,
  val size: BlockCoordinate
) {
  companion object {
    fun zeroOffsetAndMax(offset: BlockCoordinate, max: BlockCoordinate) = BlockExpanse(
      offset,
      offset.applyAsOffset(max)
    )
  }
}

package cloud.kubelet.foundation.gjallarhorn.state

class BlockExpanse(
  val offset: BlockCoordinate,
  val size: BlockCoordinate
) {
  companion object {
    fun offsetAndMax(offset: BlockCoordinate, max: BlockCoordinate) = BlockExpanse(
      offset,
      offset.applyAsOffset(max)
    )
  }
}

package cloud.kubelet.foundation.gjallarhorn.render

class BlockExpanse(
  val offset: BlockPosition,
  val size: BlockPosition
) {
  companion object {
    fun offsetAndMax(offset: BlockPosition, max: BlockPosition) = BlockExpanse(
      offset,
      offset.applyAsOffset(max)
    )
  }
}

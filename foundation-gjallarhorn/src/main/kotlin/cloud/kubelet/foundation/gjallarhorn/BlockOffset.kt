package cloud.kubelet.foundation.gjallarhorn

data class BlockOffset(
  val x: Long,
  val y: Long,
  val z: Long
) {
  fun apply(position: BlockPosition) = position.copy(
    x = position.x + x,
    y = position.y + y,
    z = position.z + z
  )

  companion object {
    val none = BlockOffset(0, 0, 0)
  }
}

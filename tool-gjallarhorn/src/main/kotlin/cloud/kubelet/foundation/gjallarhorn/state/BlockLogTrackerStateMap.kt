package cloud.kubelet.foundation.gjallarhorn.state

class BlockLogTrackerStateMap(val tracker: BlockLogTracker) : BlockStateMap {
  override fun get(position: BlockCoordinate): BlockState? = tracker.get(position)

  override fun getVerticalSection(x: Long, z: Long): Map<Long, BlockState> {
    return tracker.blocks.filter { it.key.x == x && it.key.z == z }.mapKeys { it.key.y }
  }

  override fun getXSection(x: Long): Map<Long, Map<Long, BlockState>>? {
    throw RuntimeException("X section not supported.")
  }

  override fun put(position: BlockCoordinate, value: BlockState) {
    throw RuntimeException("Modification not supported.")
  }

  override fun createOrModify(position: BlockCoordinate, create: () -> BlockState, modify: (BlockState) -> Unit) {
    throw RuntimeException("Modification not supported.")
  }
}

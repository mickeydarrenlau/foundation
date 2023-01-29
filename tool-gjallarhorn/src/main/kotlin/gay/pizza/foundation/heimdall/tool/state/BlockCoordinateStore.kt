package gay.pizza.foundation.heimdall.tool.state

interface BlockCoordinateStore<T> {
  fun get(position: BlockCoordinate): T?
  fun getVerticalSection(x: Long, z: Long): Map<Long, T>?
  fun getXSection(x: Long): Map<Long, Map<Long, T>>?
  fun put(position: BlockCoordinate, value: T)
  fun createOrModify(position: BlockCoordinate, create: () -> T, modify: (T) -> Unit)
}

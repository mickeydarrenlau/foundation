package cloud.kubelet.foundation.gjallarhorn.state

import cloud.kubelet.foundation.gjallarhorn.util.maxOfAll
import cloud.kubelet.foundation.gjallarhorn.util.minOfAll
import java.util.*
import kotlin.math.absoluteValue

open class BlockCoordinateSparseMap<T> : BlockCoordinateStore<T> {
  private var internalBlocks = TreeMap<Long, TreeMap<Long, TreeMap<Long, T>>>()

  val blocks: TreeMap<Long, TreeMap<Long, TreeMap<Long, T>>>
    get() = internalBlocks

  override fun get(position: BlockCoordinate): T? = internalBlocks[position.x]?.get(position.z)?.get(position.z)
  override fun getVerticalSection(x: Long, z: Long): Map<Long, T>? = internalBlocks[x]?.get(z)
  override fun getXSection(x: Long): Map<Long, Map<Long, T>>? = internalBlocks[x]

  override fun put(position: BlockCoordinate, value: T) {
    internalBlocks.getOrPut(position.x) {
      TreeMap()
    }.getOrPut(position.z) {
      TreeMap()
    }[position.y] = value
  }

  override fun createOrModify(position: BlockCoordinate, create: () -> T, modify: (T) -> Unit) {
    val existing = get(position)
    if (existing == null) {
      put(position, create())
    } else {
      modify(existing)
    }
  }

  fun coordinateSequence(): Sequence<BlockCoordinate> = internalBlocks.asSequence().flatMap { x ->
    x.value.asSequence().flatMap { z ->
      z.value.asSequence().map { y -> BlockCoordinate(x.key, z.key, y.key) }
    }
  }

  fun calculateZeroBlockOffset(): BlockCoordinate {
    val (x, y, z) = coordinateSequence().minOfAll(3) { listOf(it.x, it.y, it.z) }
    val xOffset = if (x < 0) x.absoluteValue else 0
    val yOffset = if (y < 0) y.absoluteValue else 0
    val zOffset = if (z < 0) z.absoluteValue else 0
    return BlockCoordinate(xOffset, yOffset, zOffset)
  }

  fun calculateMaxBlock(): BlockCoordinate {
    val (x, y, z) = coordinateSequence().maxOfAll(3) { listOf(it.x, it.y, it.z) }
    return BlockCoordinate(x, y, z)
  }

  fun applyCoordinateOffset(offset: BlockCoordinate) {
    val root = TreeMap<Long, TreeMap<Long, TreeMap<Long, T>>>()
    internalBlocks = internalBlocks.map { xSection ->
      val zSectionMap = TreeMap<Long, TreeMap<Long, T>>()
      (xSection.key + offset.x) to xSection.value.map { zSection ->
        val ySectionMap = TreeMap<Long, T>()
        (zSection.key + offset.z) to zSection.value.map { ySection ->
          (ySection.key + offset.y) to ySection.value
        }.toMap(ySectionMap)
      }.toMap(zSectionMap)
    }.toMap(root)
  }
}

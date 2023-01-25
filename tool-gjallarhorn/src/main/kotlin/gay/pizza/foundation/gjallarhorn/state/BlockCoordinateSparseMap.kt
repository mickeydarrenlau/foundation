package gay.pizza.foundation.gjallarhorn.state

import gay.pizza.foundation.gjallarhorn.util.maxOfAll
import gay.pizza.foundation.gjallarhorn.util.minOfAll
import kotlin.math.absoluteValue

open class BlockCoordinateSparseMap<T>(blocks: Map<Long, Map<Long, Map<Long, T>>> = mutableMapOf()) : BlockCoordinateStore<T> {
  private var internalBlocks = blocks

  val blocks: Map<Long, Map<Long, Map<Long, T>>>
    get() = internalBlocks

  override fun get(position: BlockCoordinate): T? = internalBlocks[position.x]?.get(position.z)?.get(position.z)
  override fun getVerticalSection(x: Long, z: Long): Map<Long, T>? = internalBlocks[x]?.get(z)
  override fun getXSection(x: Long): Map<Long, Map<Long, T>>? = internalBlocks[x]

  override fun put(position: BlockCoordinate, value: T) {
    (((internalBlocks as MutableMap).getOrPut(position.x) {
      mutableMapOf()
    } as MutableMap).getOrPut(position.z) {
      mutableMapOf()
    } as MutableMap)[position.y] = value
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
    val root = mutableMapOf<Long, MutableMap<Long, MutableMap<Long, T>>>()
    internalBlocks = internalBlocks.map { xSection ->
      val zSectionMap = mutableMapOf<Long, MutableMap<Long, T>>()
      (xSection.key + offset.x) to xSection.value.map { zSection ->
        val ySectionMap = mutableMapOf<Long, T>()
        (zSection.key + offset.z) to zSection.value.mapKeys {
          (it.key + offset.y)
        }.toMap(ySectionMap)
      }.toMap(zSectionMap)
    }.toMap(root)
  }
}

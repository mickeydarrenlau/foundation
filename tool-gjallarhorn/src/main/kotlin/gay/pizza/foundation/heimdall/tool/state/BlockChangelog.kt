package gay.pizza.foundation.heimdall.tool.state

import gay.pizza.foundation.heimdall.table.BlockChangeTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Duration
import java.time.Instant
import java.util.stream.Stream

class BlockChangelog(
  val changes: List<RecordedBlockChange>
) {
  fun slice(slice: ChangelogSlice): BlockChangelog = BlockChangelog(changes.filter {
    slice.isTimeWithinFullRange(it.time)
  })

  fun countRelativeChangesInSlice(slice: ChangelogSlice): Int = changes.count {
    slice.isTimeWithinSliceRange(it.time)
  }

  val fullTimeSlice: ChangelogSlice
    get() = ChangelogSlice(changes.minOf { it.time }, changes.maxOf { it.time })

  fun calculateChangelogSlices(interval: Duration, limit: Int? = null): List<ChangelogSlice> {
    val timeSlice = fullTimeSlice
    val start = timeSlice.rootStartTime
    val end = timeSlice.sliceEndTime
    var intervals = mutableListOf<Instant>()
    var current = start
    while (!current.isAfter(end)) {
      intervals.add(current)
      current = current.plus(interval)
    }

    if (limit != null) {
      intervals = intervals.takeLast(limit).toMutableList()
    }
    return intervals.map { ChangelogSlice(start, it, interval) }
  }

  fun splitChangelogSlicesWithThreshold(
    targetChangeThreshold: Int,
    minimumTimeInterval: Duration,
    slices: List<ChangelogSlice>
  ): List<ChangelogSlice> {
    return slices.parallelStream().flatMap { slice ->
      val count = countRelativeChangesInSlice(slice)
      if (count < targetChangeThreshold ||
        slice.sliceRelativeDuration < minimumTimeInterval
      ) {
        return@flatMap Stream.of(slice)
      }

      val split = slice.split()
      return@flatMap splitChangelogSlicesWithThreshold(targetChangeThreshold, minimumTimeInterval, split).parallelStream()
    }.toList()
  }

  companion object {
    fun query(db: Database, filter: Op<Boolean> = Op.TRUE): BlockChangelog = transaction(db) {
      BlockChangelog(BlockChangeTable.select(filter).orderBy(BlockChangeTable.time).map { row ->
        val time = row[BlockChangeTable.time]
        val world = row[BlockChangeTable.world]
        val x = row[BlockChangeTable.x]
        val y = row[BlockChangeTable.y]
        val z = row[BlockChangeTable.z]
        val blockMaterial = row[BlockChangeTable.block]
        val blockData = row[BlockChangeTable.data]
        val location = BlockCoordinate(x.toLong(), y.toLong(), z.toLong())

        val block = BlockState(blockMaterial, blockData)

        RecordedBlockChange(
          time,
          world,
          location,
          block
        )
      })
    }
  }

  fun <T> splitBy(key: (RecordedBlockChange) -> T): Map<T, BlockChangelog> {
    val logs = mutableMapOf<T, MutableList<RecordedBlockChange>>()
    for (change in changes) {
      val k = key(change)
      var log = logs[k]
      if (log == null) {
        log = mutableListOf()
        logs[k] = log
      }
      log.add(change)
    }
    return logs.mapValues { BlockChangelog(it.value) }
  }
}

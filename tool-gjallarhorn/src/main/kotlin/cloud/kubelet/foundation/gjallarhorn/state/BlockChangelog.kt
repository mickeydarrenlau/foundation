package cloud.kubelet.foundation.gjallarhorn.state

import cloud.kubelet.foundation.heimdall.view.BlockChangeView
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Duration
import java.time.Instant
import java.util.stream.Stream

class BlockChangelog(
  val changes: List<BlockChange>
) {
  fun slice(slice: ChangelogSlice): BlockChangelog = BlockChangelog(changes.filter {
    slice.isTimeWithin(it.time)
  })

  fun countRelativeChangesInSlice(slice: ChangelogSlice): Int = changes.count {
    slice.isRelativeWithin(it.time)
  }

  val changeTimeRange: ChangelogSlice
    get() = ChangelogSlice(changes.minOf { it.time }, changes.maxOf { it.time })

  fun calculateChangelogSlices(interval: Duration, limit: Int? = null): List<ChangelogSlice> {
    val (start, end) = changeTimeRange
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
        slice.relative < minimumTimeInterval
      ) {
        return@flatMap Stream.of(slice)
      }

      val split = slice.split()
      return@flatMap splitChangelogSlicesWithThreshold(targetChangeThreshold, minimumTimeInterval, split).parallelStream()
    }.toList()
  }

  companion object {
    fun query(db: Database, filter: Op<Boolean> = Op.TRUE): BlockChangelog = transaction(db) {
      BlockChangelog(BlockChangeView.select(filter).orderBy(BlockChangeView.time).map { row ->
        val time = row[BlockChangeView.time]
        val changeIsBreak = row[BlockChangeView.isBreak]
        val x = row[BlockChangeView.x]
        val y = row[BlockChangeView.y]
        val z = row[BlockChangeView.z]
        val block = row[BlockChangeView.block]
        val location = BlockCoordinate(x.toLong(), y.toLong(), z.toLong())

        val fromBlock = if (changeIsBreak) {
          BlockState.cached(block)
        } else {
          BlockState.AirBlock
        }

        val toBlock = if (changeIsBreak) {
          BlockState.AirBlock
        } else {
          BlockState.cached(block)
        }

        BlockChange(
          time,
          if (changeIsBreak) BlockChangeType.Break else BlockChangeType.Place,
          location,
          fromBlock,
          toBlock
        )
      })
    }
  }
}

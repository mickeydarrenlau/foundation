package cloud.kubelet.foundation.gjallarhorn.state

import cloud.kubelet.foundation.heimdall.view.BlockChangeView
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class BlockChangelog(
  val changes: List<BlockChange>
) {
  fun slice(slice: BlockChangelogSlice): BlockChangelog = BlockChangelog(changes.filter {
    it.time >= slice.first &&
        it.time <= slice.second
  })

  val changeTimeRange: BlockChangelogSlice
    get() = changes.minOf { it.time } to changes.maxOf { it.time }

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
          BlockState(block)
        } else {
          BlockState.AirBlock
        }

        val toBlock = if (changeIsBreak) {
          BlockState.AirBlock
        } else {
          BlockState(block)
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

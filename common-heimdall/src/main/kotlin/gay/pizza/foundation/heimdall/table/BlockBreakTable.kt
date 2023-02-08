package gay.pizza.foundation.heimdall.table

object BlockBreakTable : PlayerTimedLocalEventTable("block_breaks") {
  val block = text("block")
  val blockData = text("block_data").nullable()
}

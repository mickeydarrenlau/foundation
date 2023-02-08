package gay.pizza.foundation.heimdall.table

object BlockChangeTable : PlayerTimedLocalEventTable("block_changes") {
  val block = text("block")
  val data = text("data")
  val cause = text("cause")
}

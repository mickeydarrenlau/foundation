package gay.pizza.foundation.heimdall.table

object BlockPlaceTable : PlayerTimedLocalEventTable("block_places") {
  val block = text("block")
  val blockData = text("block_data").nullable()
}

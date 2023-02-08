package gay.pizza.foundation.heimdall.view

import gay.pizza.foundation.heimdall.table.PlayerTimedLocalEventTable

object BlockChangeView : PlayerTimedLocalEventTable("block_changes") {
  val isBreak = bool("break")
  val block = text("block")
  val blockData = text("block_data").nullable()
}

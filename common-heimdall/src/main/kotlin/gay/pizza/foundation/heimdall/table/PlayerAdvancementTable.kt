package gay.pizza.foundation.heimdall.table

object PlayerAdvancementTable : PlayerTimedLocalEventTable("player_advancements") {
  val advancement = text("advancement")
}

package gay.pizza.foundation.heimdall.table

object EntityKillTable : PlayerTimedLocalEventTable("entity_kills") {
  val entity = uuid("entity")
  val entityType = text("entity_type")
}

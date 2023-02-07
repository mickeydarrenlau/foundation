package gay.pizza.foundation.heimdall.table

object WorldChangeTable : TimedEventTable("world_changes") {
  val player = uuid("player")
  val fromWorld = uuid("from_world")
  val toWorld = uuid("to_world")
  val fromWorldName = text("from_world_name")
  val toWorldName = text("to_world_name")
}

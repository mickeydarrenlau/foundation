package gay.pizza.foundation.heimdall.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object EntityKillTable : Table("entity_kills") {
  val time = timestamp("time")
  val player = uuid("player")
  val entity = uuid("entity")
  val world = uuid("world")
  val x = double("x")
  val y = double("y")
  val z = double("z")
  val pitch = double("pitch")
  val yaw = double("yaw")
  val entityType = text("entity_type")
}

package gay.pizza.foundation.heimdall.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object BlockPlaceTable : Table("block_places") {
  val time = timestamp("time")
  val player = uuid("player")
  val world = uuid("world")
  val x = double("x")
  val y = double("y")
  val z = double("z")
  val pitch = double("pitch")
  val yaw = double("yaw")
  val block = text("block")
}

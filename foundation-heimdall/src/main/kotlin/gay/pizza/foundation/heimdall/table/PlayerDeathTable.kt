package gay.pizza.foundation.heimdall.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object PlayerDeathTable : Table("player_deaths") {
  val time = timestamp("time")
  val world = uuid("world")
  val player = uuid("player")
  val x = double("x")
  val y = double("y")
  val z = double("z")
  val pitch = double("pitch")
  val yaw = double("yaw")
  val experience = double("experience")
  val message = text("message").nullable()
}

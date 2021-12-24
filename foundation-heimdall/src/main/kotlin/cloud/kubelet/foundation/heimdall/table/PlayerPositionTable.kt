package cloud.kubelet.foundation.heimdall.table

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.timestamp

object PlayerPositionTable : Table("player_positions") {
  val time = timestamp("time")
  val world = uuid("world")
  val player = uuid("player")
  val x = double("x")
  val y = double("y")
  val z = double("z")
  val pitch = double("pitch")
  val yaw = double("yaw")
}

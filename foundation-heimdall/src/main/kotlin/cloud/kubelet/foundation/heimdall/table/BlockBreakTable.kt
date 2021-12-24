package cloud.kubelet.foundation.heimdall.table

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.timestamp

object BlockBreakTable : Table("block_breaks") {
  val time = timestamp("time")
  val world = uuid("world")
  val player = uuid("player")
  val block = text("block")
  val x = double("x")
  val y = double("y")
  val z = double("z")
}

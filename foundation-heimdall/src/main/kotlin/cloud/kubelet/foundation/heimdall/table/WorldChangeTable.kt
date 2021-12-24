package cloud.kubelet.foundation.heimdall.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object WorldChangeTable : Table("world_changes") {
  val time = timestamp("time")
  val player = uuid("player")
  val fromWorld = uuid("from_world")
  val toWorld = uuid("to_world")
  val fromWorldName = text("from_world_name")
  val toWorldName = text("to_world_name")
}

package cloud.kubelet.foundation.heimdall.table

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.timestamp

object PlayerSessionTable : Table("player_sessions") {
  val id = uuid("id")
  val player = uuid("player")
  val name = text("name")
  val startTime = timestamp("start")
  val endTime = timestamp("end")
}

package gay.pizza.foundation.heimdall.plugin.event

import gay.pizza.foundation.heimdall.table.PlayerTimedLocalEventTable
import gay.pizza.foundation.heimdall.table.TimedEventTable
import gay.pizza.foundation.heimdall.table.TimedLocalEventTable
import org.bukkit.Location
import org.jetbrains.exposed.sql.statements.InsertStatement
import java.time.Instant
import java.util.*

fun <T : TimedEventTable, K : Any> T.putTimedEvent(statement: InsertStatement<K>, time: Instant) {
  statement[this.time] = time
}

fun <T : TimedLocalEventTable, K : Any> T.putTimedLocalEvent(
  statement: InsertStatement<K>,
  time: Instant,
  location: Location
) {
  statement[this.time] = time
  statement[this.world] = location.world.uid
  statement[this.x] = location.x
  statement[this.y] = location.y
  statement[this.z] = location.z
}

fun <T : PlayerTimedLocalEventTable, K : Any> T.putPlayerTimedLocalEvent(
  statement: InsertStatement<K>,
  time: Instant,
  location: Location,
  player: UUID
) {
  putTimedLocalEvent(statement, time, location)
  statement[this.player] = player
  statement[this.pitch] = location.pitch.toDouble()
  statement[this.yaw] = location.yaw.toDouble()
}

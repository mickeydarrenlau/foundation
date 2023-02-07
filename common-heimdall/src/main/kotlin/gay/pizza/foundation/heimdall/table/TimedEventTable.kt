package gay.pizza.foundation.heimdall.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

abstract class TimedEventTable(name: String) : Table(name) {
  val time = timestamp("time")
}

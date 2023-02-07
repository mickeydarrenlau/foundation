package gay.pizza.foundation.heimdall.table

abstract class TimedLocalEventTable(name: String) : TimedEventTable(name) {
  val world = uuid("world")
  val x = double("x")
  val y = double("y")
  val z = double("z")
}

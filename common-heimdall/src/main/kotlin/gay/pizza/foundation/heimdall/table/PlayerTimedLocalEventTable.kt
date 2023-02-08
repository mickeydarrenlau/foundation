package gay.pizza.foundation.heimdall.table

abstract class PlayerTimedLocalEventTable(name: String) : TimedLocalEventTable(name) {
  val player = uuid("player").nullable()
  val pitch = double("pitch")
  val yaw = double("yaw")
}

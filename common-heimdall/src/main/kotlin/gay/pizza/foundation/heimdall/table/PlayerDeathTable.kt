package gay.pizza.foundation.heimdall.table

object PlayerDeathTable : PlayerTimedLocalEventTable("player_deaths") {
  val experience = double("experience")
  val message = text("message").nullable()
}

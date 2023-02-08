package gay.pizza.foundation.heimdall.plugin.event

object EventCollectorProviders {
  val all = listOf<EventCollectorProvider<*>>(
    BlockChange,
    EntityKill,
    PlayerAdvancement,
    PlayerDeath,
    PlayerPosition,
    PlayerSession,
    WorldChange
  )
}

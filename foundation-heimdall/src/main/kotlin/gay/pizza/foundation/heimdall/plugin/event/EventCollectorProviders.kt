package gay.pizza.foundation.heimdall.plugin.event

object EventCollectorProviders {
  val all = listOf<EventCollectorProvider<*>>(
    BlockBreak,
    BlockPlace,
    EntityKill,
    PlayerAdvancement,
    PlayerDeath,
    PlayerPosition,
    PlayerSession,
    WorldChange
  )
}

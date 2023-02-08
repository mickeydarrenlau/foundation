package gay.pizza.foundation.heimdall.load

import kotlinx.serialization.Serializable

@Serializable
class WorldLoadFormat(
  val worlds: Map<String, WorldLoadWorld>
)

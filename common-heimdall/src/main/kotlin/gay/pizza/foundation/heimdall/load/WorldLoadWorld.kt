package gay.pizza.foundation.heimdall.load

import kotlinx.serialization.Serializable

@Serializable
sealed class WorldLoadWorld {
  abstract val name: String

  abstract fun crawl(block: (Long, Long, Long, Int) -> Unit)
}

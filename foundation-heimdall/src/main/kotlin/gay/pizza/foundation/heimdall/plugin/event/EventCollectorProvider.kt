package gay.pizza.foundation.heimdall.plugin.event

import gay.pizza.foundation.heimdall.plugin.buffer.EventBuffer
import gay.pizza.foundation.heimdall.plugin.model.HeimdallConfig

interface EventCollectorProvider<T : HeimdallEvent> {
  fun collector(config: HeimdallConfig, buffer: EventBuffer): EventCollector<T>
}

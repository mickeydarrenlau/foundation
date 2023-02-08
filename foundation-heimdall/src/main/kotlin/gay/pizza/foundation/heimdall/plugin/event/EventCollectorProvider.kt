package gay.pizza.foundation.heimdall.plugin.event

import gay.pizza.foundation.heimdall.plugin.buffer.EventBuffer

interface EventCollectorProvider<T : HeimdallEvent> {
  fun collector(buffer: EventBuffer): EventCollector<T>
}

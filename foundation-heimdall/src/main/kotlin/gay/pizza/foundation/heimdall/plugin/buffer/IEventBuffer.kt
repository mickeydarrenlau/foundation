package gay.pizza.foundation.heimdall.plugin.buffer

import gay.pizza.foundation.heimdall.plugin.event.HeimdallEvent

interface IEventBuffer {
  fun push(event: HeimdallEvent)
  fun pushAll(events: List<HeimdallEvent>)
}

package gay.pizza.foundation.heimdall.plugin.buffer

import gay.pizza.foundation.heimdall.plugin.event.HeimdallEvent
import org.jetbrains.exposed.sql.Transaction

class EventBuffer : IEventBuffer {
  private var events = mutableListOf<HeimdallEvent>()

  fun flush(transaction: Transaction): Long {
    val referenceOfEvents = events
    this.events = mutableListOf()
    var count = 0L
    while (referenceOfEvents.isNotEmpty()) {
      val event = referenceOfEvents.removeAt(0)
      event.store(transaction, count.toInt())
      count++
    }
    return count
  }

  override fun push(event: HeimdallEvent) {
    events.add(event)
  }

  override fun pushAll(events: List<HeimdallEvent>) {
    this.events.addAll(events)
  }

  fun clear() {
    events = mutableListOf()
  }
}

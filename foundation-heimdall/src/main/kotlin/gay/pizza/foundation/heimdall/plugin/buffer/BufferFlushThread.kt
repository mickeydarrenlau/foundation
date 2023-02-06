package gay.pizza.foundation.heimdall.plugin.buffer

import gay.pizza.foundation.heimdall.plugin.FoundationHeimdallPlugin
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.atomic.AtomicBoolean

class BufferFlushThread(val plugin: FoundationHeimdallPlugin, val buffer: EventBuffer) {
  private val running = AtomicBoolean(false)
  private var thread: Thread? = null

  fun start() {
    running.set(true)
    val thread = Thread {
      plugin.slF4JLogger.info("Buffer Flusher Started")
      while (running.get()) {
        flush()
        Thread.sleep(5000)
      }
      plugin.slF4JLogger.info("Buffer Flusher Stopped")
    }
    thread.name = "Heimdall Buffer Flush"
    thread.isDaemon = false
    thread.start()
    this.thread = thread
  }

  fun stop() {
    running.set(false)
    thread?.join()
  }

  fun flush() {
    try {
      val db = plugin.db
      if (db == null) {
        buffer.clear()
        return
      }
      transaction(plugin.db) {
        val count = buffer.flush(this)
        if (count > 0) {
          plugin.slF4JLogger.debug("Flushed $count Events")
        }
      }
    } catch (e: Exception) {
      plugin.slF4JLogger.warn("Failed to flush buffer.", e)
    }
  }
}

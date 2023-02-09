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
      plugin.slF4JLogger.info("Buffer flusher started.")
      while (running.get()) {
        flush()
        Thread.sleep(5000)
      }
      plugin.slF4JLogger.info("Buffer flusher stopped.")
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
      for (collector in plugin.collectors) {
        collector.beforeBufferFlush()
      }

      val db = plugin.db
      if (db == null) {
        buffer.clear()
        return
      }
      transaction(plugin.db) {
        val count = buffer.flush(this)
        if (count > 0) {
          plugin.slF4JLogger.debug("Flushed $count events.")
        }
      }
    } catch (e: Exception) {
      plugin.slF4JLogger.warn("Failed to flush buffer.", e)
    }
  }
}

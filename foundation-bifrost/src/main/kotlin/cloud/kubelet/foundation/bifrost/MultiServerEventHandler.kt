package cloud.kubelet.foundation.bifrost

import cloud.kubelet.foundation.bifrost.model.BifrostMessageQueueConfig
import cloud.kubelet.foundation.bifrost.model.BifrostMultiConfig
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import io.papermc.paper.event.player.AsyncChatEvent

class MultiServerEventHandler(config: BifrostMultiConfig) : EventHandler {
  private val bus = buildConnection(config.messageQueue)
  private val channel = bus.createChannel()

  init {
    channel.queueDeclare(config.messageQueue.queueName, false, false, false, emptyMap())
  }

  override fun onChat(e: AsyncChatEvent) {
  }

  private companion object {
    fun buildConnection(config: BifrostMessageQueueConfig): Connection = ConnectionFactory().apply {
      host = config.host
      port = config.port
    }.newConnection()
  }
}

package cloud.kubelet.foundation.bifrost.model

import kotlinx.serialization.Serializable

@Serializable
data class BifrostMultiConfig(
  val messageQueue: BifrostMessageQueueConfig,
)

@Serializable
data class BifrostMessageQueueConfig(
  val host: String = "localhost",
  val port: Int = 5672,

  /**
   * Name of the RabbitMQ queue
   */
  val queueName: String = "bifrost",
)

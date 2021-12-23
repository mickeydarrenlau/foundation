package cloud.kubelet.foundation.core.features.stats

import cloud.kubelet.foundation.core.FoundationCorePlugin
import cloud.kubelet.foundation.core.abstraction.Feature
import cloud.kubelet.foundation.core.persist.PersistentStore
import cloud.kubelet.foundation.core.persist.setAllProperties
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.TextComponent
import org.bukkit.event.EventHandler
import org.koin.core.component.inject
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

class StatsFeature : Feature() {
  private val plugin = inject<FoundationCorePlugin>()
  private lateinit var chatLogStore: PersistentStore
  // TODO: Move persistence stuff to its own module.
  internal val persistentStores = ConcurrentHashMap<String, PersistentStore>()

  override fun enable() {
    chatLogStore = getPersistentStore("chat-logs")

    registerCommandExecutor("pstore", PersistentStoreCommand(this))
  }

  override fun disable() {
    persistentStores.values.forEach { store -> store.close() }
    persistentStores.clear()
  }

  /**
   * Fetch a persistent store by name. Make sure the name is path-safe, descriptive and consistent across server runs.
   */
  fun getPersistentStore(name: String) =
    persistentStores.getOrPut(name) { PersistentStore(plugin.value, name) }

  @EventHandler
  private fun logOnChatMessage(e: AsyncChatEvent) {
    val player = e.player
    val message = e.message()

    if (message !is TextComponent) {
      return
    }

    val content = message.content()
    chatLogStore.create("ChatMessageEvent") {
      setAllProperties(
        "timestamp" to Instant.now().toEpochMilli(),
        "player.id" to player.identity().uuid().toString(),
        "player.name" to player.name,
        "message.content" to content
      )
    }
  }
}

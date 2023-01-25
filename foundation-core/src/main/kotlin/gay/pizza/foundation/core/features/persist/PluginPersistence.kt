package gay.pizza.foundation.core.features.persist

import gay.pizza.foundation.core.FoundationCorePlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.ConcurrentHashMap

class PluginPersistence : KoinComponent {
  private val plugin = inject<FoundationCorePlugin>()

  val stores = ConcurrentHashMap<String, PersistentStore>()

  /**
   * Fetch a persistent store by name. Make sure the name is path-safe, descriptive and consistent across server runs.
   */
  fun store(name: String): PersistentStore =
    stores.getOrPut(name) { PersistentStore(plugin.value, name) }

  fun unload() {
    stores.values.forEach { store -> store.close() }
    stores.clear()
  }
}

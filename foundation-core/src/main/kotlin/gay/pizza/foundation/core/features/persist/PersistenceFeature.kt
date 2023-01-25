package gay.pizza.foundation.core.features.persist

import gay.pizza.foundation.core.abstraction.Feature
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module

class PersistenceFeature : Feature() {
  private val persistence = inject<PluginPersistence>()

  override fun disable() {
    persistence.value.unload()
  }

  override fun module(): Module = module {
    single { PluginPersistence() }
  }
}

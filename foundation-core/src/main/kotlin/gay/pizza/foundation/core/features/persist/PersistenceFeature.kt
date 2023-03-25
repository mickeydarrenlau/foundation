package gay.pizza.foundation.core.features.persist

import gay.pizza.foundation.core.FoundationCorePlugin
import gay.pizza.foundation.core.abstraction.Feature
import gay.pizza.foundation.shared.PluginPersistence
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module

class PersistenceFeature : Feature() {
  private val persistence by inject<PluginPersistence>()
  private val core by inject<FoundationCorePlugin>()

  override fun disable() {
    persistence.unload()
  }

  override fun module(): Module = module {
    single { core.persistence }
  }
}

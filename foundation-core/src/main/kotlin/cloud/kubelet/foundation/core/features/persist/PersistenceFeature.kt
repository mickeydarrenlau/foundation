package cloud.kubelet.foundation.core.features.persist

import cloud.kubelet.foundation.core.abstraction.Feature
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

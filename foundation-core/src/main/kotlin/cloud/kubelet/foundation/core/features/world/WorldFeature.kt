package cloud.kubelet.foundation.core.features.world

import cloud.kubelet.foundation.core.abstraction.Feature

class WorldFeature : Feature() {
  override fun enable() {
    registerCommandExecutor("setspawn", SetSpawnCommand())
    registerCommandExecutor("spawn", SpawnCommand())
  }
}

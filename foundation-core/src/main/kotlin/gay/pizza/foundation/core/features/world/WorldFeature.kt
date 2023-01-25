package gay.pizza.foundation.core.features.world

import gay.pizza.foundation.core.abstraction.Feature

class WorldFeature : Feature() {
  override fun enable() {
    registerCommandExecutor("setspawn", SetSpawnCommand())
    registerCommandExecutor("spawn", SpawnCommand())
  }
}

package gay.pizza.foundation.core.features.world

import gay.pizza.foundation.core.abstraction.Feature

class WorldFeature : Feature() {
  override fun enable() {
    plugin.registerCommandExecutor("setspawn", SetSpawnCommand())
    plugin.registerCommandExecutor("spawn", SpawnCommand())
  }
}

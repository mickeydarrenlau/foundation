package gay.pizza.foundation.core.features.dev

import gay.pizza.foundation.core.abstraction.Feature

class DevFeature : Feature() {
  private lateinit var devUpdateServer: DevUpdateServer

  override fun enable() {
    devUpdateServer = DevUpdateServer(plugin)
    devUpdateServer.enable()
  }

  override fun disable() {
    devUpdateServer.disable()
  }
}

package cloud.kubelet.foundation.core.features.dev

import cloud.kubelet.foundation.core.FoundationCorePlugin
import cloud.kubelet.foundation.core.abstraction.Feature
import cloud.kubelet.foundation.core.devupdate.DevUpdateServer
import org.koin.core.component.inject

class DevFeature : Feature() {
  private val plugin = inject<FoundationCorePlugin>()
  private lateinit var devUpdateServer: DevUpdateServer

  override fun enable() {
    devUpdateServer = DevUpdateServer(plugin.value)
    devUpdateServer.enable()
  }

  override fun disable() {
    devUpdateServer.disable()
  }
}

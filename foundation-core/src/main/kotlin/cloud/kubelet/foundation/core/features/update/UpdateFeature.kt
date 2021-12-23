package cloud.kubelet.foundation.core.features.update

import cloud.kubelet.foundation.core.abstraction.Feature

class UpdateFeature : Feature() {
  override fun enable() {
    registerCommandExecutor("fupdate", UpdateCommand())
  }
}

package gay.pizza.foundation.core.features.update

import gay.pizza.foundation.core.abstraction.Feature

class UpdateFeature : Feature() {
  override fun enable() {
    registerCommandExecutor("fupdate", UpdateCommand())
  }
}

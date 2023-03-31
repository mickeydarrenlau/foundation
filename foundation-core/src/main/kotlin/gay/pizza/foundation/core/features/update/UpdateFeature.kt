package gay.pizza.foundation.core.features.update

import gay.pizza.foundation.core.abstraction.Feature
import gay.pizza.foundation.core.features.scheduler.cancel
import gay.pizza.foundation.core.features.scheduler.cron
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module

class UpdateFeature : Feature() {
  private val config by inject<UpdateConfig>()
  lateinit var autoUpdateScheduleId: String

  override fun enable() {
    plugin.registerCommandExecutor("fupdate", UpdateCommand(plugin))

    if (config.autoUpdateSchedule.cron.isNotEmpty()) {
      autoUpdateScheduleId = scheduler.cron(config.autoUpdateSchedule.cron) {
        plugin.server.scheduler.runTask(plugin) { ->
          plugin.server.dispatchCommand(plugin.server.consoleSender, "fupdate restart")
        }
      }
    }
  }

  override fun disable() {
    if (::autoUpdateScheduleId.isInitialized) {
      scheduler.cancel(autoUpdateScheduleId)
    }
  }

  override fun module(): Module = module {
    single {
      plugin.loadConfigurationWithDefault(
        plugin,
        UpdateConfig.serializer(),
        "update.yaml"
      )
    }
  }
}

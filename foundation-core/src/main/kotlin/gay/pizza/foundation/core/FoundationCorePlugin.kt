package gay.pizza.foundation.core

import gay.pizza.foundation.core.abstraction.FoundationPlugin
import gay.pizza.foundation.core.features.backup.BackupFeature
import gay.pizza.foundation.core.features.dev.DevFeature
import gay.pizza.foundation.core.features.gameplay.GameplayFeature
import gay.pizza.foundation.core.features.persist.PersistenceFeature
import gay.pizza.foundation.core.features.player.PlayerFeature
import gay.pizza.foundation.core.features.scheduler.SchedulerFeature
import gay.pizza.foundation.core.features.stats.StatsFeature
import gay.pizza.foundation.core.features.update.UpdateFeature
import gay.pizza.foundation.core.features.world.WorldFeature
import gay.pizza.foundation.shared.IFoundationCore
import gay.pizza.foundation.shared.PluginMainClass
import gay.pizza.foundation.shared.PluginPersistence
import org.koin.dsl.module
import java.nio.file.Path

@PluginMainClass
class FoundationCorePlugin : IFoundationCore, FoundationPlugin() {
  private lateinit var _pluginDataPath: Path

  override var pluginDataPath: Path
    /**
     * Data path of the core plugin.
     * Can be used as a check of sorts for dependencies to be sure the plugin is loaded.
     */
    get() {
      if (!::_pluginDataPath.isInitialized) {
        throw Exception("Foundation Core is not loaded!")
      }
      return _pluginDataPath
    }
    private set(value) {
      _pluginDataPath = value
    }

  override val persistence: PluginPersistence = PluginPersistence(this)

  override fun onEnable() {
    // Create core plugin directory.
    pluginDataPath = dataFolder.toPath()
    pluginDataPath.toFile().mkdir()

    super.onEnable()
  }

  override fun createFeatures() = listOf(
    SchedulerFeature(),
    PersistenceFeature(),
    BackupFeature(),
    DevFeature(),
    GameplayFeature(),
    PlayerFeature(),
    StatsFeature(),
    UpdateFeature(),
    WorldFeature(),
  )

  override fun createModule() = module {
    single { this@FoundationCorePlugin }
  }
}

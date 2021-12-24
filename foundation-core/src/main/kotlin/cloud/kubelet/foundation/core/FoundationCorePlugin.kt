package cloud.kubelet.foundation.core

import cloud.kubelet.foundation.core.abstraction.FoundationPlugin
import cloud.kubelet.foundation.core.features.backup.BackupFeature
import cloud.kubelet.foundation.core.features.dev.DevFeature
import cloud.kubelet.foundation.core.features.player.PlayerFeature
import cloud.kubelet.foundation.core.features.stats.StatsFeature
import cloud.kubelet.foundation.core.features.update.UpdateFeature
import cloud.kubelet.foundation.core.features.world.WorldFeature
import cloud.kubelet.foundation.core.features.persist.PersistenceFeature
import org.koin.dsl.module
import java.nio.file.Path

class FoundationCorePlugin : FoundationPlugin() {
  private lateinit var _pluginDataPath: Path

  var pluginDataPath: Path
    /**
     * Data path of the core plugin.
     * Can be used as a sanity check of sorts for dependencies to be sure the plugin is loaded.
     */
    get() {
      if (!::_pluginDataPath.isInitialized) {
        throw Exception("FoundationCore is not loaded!")
      }
      return _pluginDataPath
    }
    private set(value) {
      _pluginDataPath = value
    }

  override fun onEnable() {
    // Create core plugin directory.
    pluginDataPath = dataFolder.toPath()
    pluginDataPath.toFile().mkdir()

    super.onEnable()
  }

  override fun createFeatures() = listOf(
    PersistenceFeature(),
    BackupFeature(),
    DevFeature(),
    PlayerFeature(),
    StatsFeature(),
    UpdateFeature(),
    WorldFeature(),
  )

  override fun createModule() = module {
    single { this@FoundationCorePlugin }
  }
}

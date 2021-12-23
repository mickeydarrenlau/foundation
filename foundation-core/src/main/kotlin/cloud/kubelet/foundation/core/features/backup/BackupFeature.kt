package cloud.kubelet.foundation.core.features.backup

import cloud.kubelet.foundation.core.FoundationCorePlugin
import cloud.kubelet.foundation.core.abstraction.Feature
import org.koin.core.component.inject

class BackupFeature : Feature() {
  private val plugin by inject<FoundationCorePlugin>()

  override fun enable() {
    // Create backup directory.
    val backupPath = plugin.pluginDataPath.resolve(BACKUPS_DIRECTORY)
    backupPath.toFile().mkdir()

    registerCommandExecutor("fbackup", BackupCommand(plugin, backupPath))
  }

  companion object {
    private const val BACKUPS_DIRECTORY = "backups"
  }
}

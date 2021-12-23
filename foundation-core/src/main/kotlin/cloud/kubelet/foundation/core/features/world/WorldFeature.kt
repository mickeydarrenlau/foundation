package cloud.kubelet.foundation.core.features.world

import cloud.kubelet.foundation.core.abstraction.Feature
import cloud.kubelet.foundation.core.command.GamemodeCommand
import cloud.kubelet.foundation.core.command.SetSpawnCommand
import cloud.kubelet.foundation.core.command.SpawnCommand
import org.bukkit.GameMode

class WorldFeature : Feature() {
  override fun enable() {
    registerCommandExecutor("setspawn", SetSpawnCommand())
    registerCommandExecutor("spawn", SpawnCommand())
  }
}

package cloud.kubelet.foundation.core.features.player

import cloud.kubelet.foundation.core.abstraction.Feature
import cloud.kubelet.foundation.core.command.GamemodeCommand
import org.bukkit.GameMode

class PlayerFeature : Feature() {
  override fun enable() {
    registerCommandExecutor(listOf("survival", "s"), GamemodeCommand(GameMode.SURVIVAL))
    registerCommandExecutor(listOf("creative", "c"), GamemodeCommand(GameMode.CREATIVE))
    registerCommandExecutor(listOf("adventure", "a"), GamemodeCommand(GameMode.ADVENTURE))
    registerCommandExecutor(listOf("spectator", "sp"), GamemodeCommand(GameMode.SPECTATOR))
  }
}

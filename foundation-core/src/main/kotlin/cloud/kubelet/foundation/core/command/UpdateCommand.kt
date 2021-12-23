package cloud.kubelet.foundation.core.command

import cloud.kubelet.foundation.core.update.UpdateUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import kotlin.io.path.name
import kotlin.io.path.toPath

class UpdateCommand : CommandExecutor {
  override fun onCommand(
    sender: CommandSender,
    command: Command,
    label: String,
    args: Array<out String>
  ): Boolean {
    val updateDir = sender.server.pluginsFolder.resolve("update")
    updateDir.mkdir()
    if (!updateDir.exists()) {
      sender.sendMessage("Error: Failed to create plugin update directory.")
      return true
    }
    val updatePath = updateDir.toPath()

    // TODO: Move to separate thread?
    val modules = UpdateUtil.fetchManifest()
    val plugins = sender.server.pluginManager.plugins.associateBy { it.name.lowercase() }

    sender.sendMessage("Updates:")
    modules.forEach { (name, manifest) ->
      // Dumb naming problem. Don't want to fix it right now.
      val plugin = if (name == "foundation-core") {
        plugins["foundation"]
      } else {
        plugins[name.lowercase()]
      }

      if (plugin == null) {
        sender.sendMessage("Plugin in manifest, but not installed: $name (${manifest.version})")
      } else {
        val fileName = plugin.javaClass.protectionDomain.codeSource.location.toURI().toPath().name
        val artifactPath = manifest.artifacts.getOrNull(0) ?: return@forEach

        sender.sendMessage("${plugin.name}: Updating ${plugin.description.version} to ${manifest.version}")
        UpdateUtil.downloadArtifact(artifactPath, updatePath.resolve(fileName))
      }
    }
    sender.sendMessage("Restart to take effect")

    return true
  }
}
package gay.pizza.foundation.core.features.update

import org.bukkit.command.CommandSender
import kotlin.io.path.name
import kotlin.io.path.toPath

// TODO: Switch to a class and use dependency injection with koin.
object UpdateService {
  fun updatePlugins(sender: CommandSender, onFinish: (() -> Unit)? = null) {
    val updateDir = sender.server.pluginsFolder.resolve("update")
    updateDir.mkdir()
    if (!updateDir.exists()) {
      sender.sendMessage("Error: Failed to create plugin update directory.")
      return
    }
    val updatePath = updateDir.toPath()

    Thread {
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

      if (onFinish != null) onFinish()
    }.start()
  }
}

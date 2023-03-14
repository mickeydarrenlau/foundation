package gay.pizza.foundation.core.features.update

import org.bukkit.command.CommandSender
import kotlin.io.path.name
import kotlin.io.path.toPath

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
      val resolver = UpdateResolver()
      val manifest = resolver.fetchCurrentManifest()
      val plan = resolver.resolve(manifest, sender.server)
      sender.sendMessage("Updates:")
      plan.items.forEach { (item, plugin) ->
        val pluginJarFileItem = item.files.firstOrNull { it.type == "plugin-jar" }
        if (pluginJarFileItem == null) {
          sender.sendMessage("WARNING: ${item.name} is required but plugin-jar file not found in manifest. Skipping.")
          return@forEach
        }
        val maybeExistingPluginFileName = plugin?.javaClass?.protectionDomain?.codeSource?.location?.toURI()?.toPath()?.name
        val fileName = maybeExistingPluginFileName ?: "${item.name}.jar"
        val artifactPath = pluginJarFileItem.path

        sender.sendMessage("${item.name}: Updating ${plugin?.description?.version ?: "[not-installed]"} to ${item.version}")
        UpdateUtil.downloadArtifact(artifactPath, updatePath.resolve(fileName))
      }
      sender.sendMessage("Restart for updates to take effect.")

      if (onFinish != null) onFinish()
    }.start()
  }
}

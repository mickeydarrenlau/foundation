package gay.pizza.foundation.core.features.update

import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.io.path.name
import kotlin.io.path.toPath

object UpdateService {
  private val running = AtomicBoolean(false)

  fun updatePlugins(plugin: Plugin, sender: CommandSender, onFinish: (Boolean) -> Unit = {}) {
    if (!running.compareAndSet(false, true)) {
      sender.sendMessage("Update is already running, skipping the requested update.")
      onFinish(false)

      return
    }
    val updateDir = sender.server.pluginsFolder.resolve("update")
    updateDir.mkdir()
    if (!updateDir.exists()) {
      sender.sendMessage("Error: Failed to create plugin update directory.")
      return
    }
    val updatePath = updateDir.toPath()

    Thread {
      try {
        val resolver = UpdateResolver()
        val manifest = resolver.fetchCurrentManifest()
        val plan = resolver.resolve(manifest, sender.server)
        if (plan.updateSet.isEmpty()) {
          onFinish(false)
          running.set(false)
          return@Thread
        }
        sender.sendMessage("Updates:")
        plan.updateSet.forEach { (item, plugin) ->
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
        onFinish(true)
      } catch (e: Exception) {
        plugin.slF4JLogger.error("Failed to update Foundation.", e)
        onFinish(false)
      } finally {
        running.set(false)
      }
    }.apply { name = "Plugin Updater" }.start()
  }
}

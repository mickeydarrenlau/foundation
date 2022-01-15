package cloud.kubelet.foundation.core.features.gameplay

import cloud.kubelet.foundation.core.FoundationCorePlugin
import cloud.kubelet.foundation.core.Util
import cloud.kubelet.foundation.core.abstraction.Feature
import com.charleskorn.kaml.Yaml
import org.bukkit.entity.EntityType
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.koin.core.component.inject
import org.koin.dsl.module
import kotlin.io.path.inputStream

class GameplayFeature : Feature() {
  private val config by inject<GameplayConfig>()

  override fun module() = module {
    single {
      val configPath = Util.copyDefaultConfig<FoundationCorePlugin>(
        plugin.slF4JLogger,
        plugin.pluginDataPath,
        "gameplay.yaml",
      )
      return@single Yaml.default.decodeFromStream(
        GameplayConfig.serializer(),
        configPath.inputStream()
      )
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  private fun onEntityDamage(e: EntityDamageEvent) {
    // If freeze damage is disabled, cancel the event.
    if (config.mobs.disableFreezeDamage) {
      if (e.entity is Mob && e.cause == EntityDamageEvent.DamageCause.FREEZE) {
        e.isCancelled = true
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  private fun onEntityChangeBlock(event: EntityChangeBlockEvent) {
    // If enderman griefing is disabled, cancel the event.
    if (config.mobs.disableEndermanGriefing) {
      if (event.entity.type == EntityType.ENDERMAN) {
        event.isCancelled = true
      }
    }
  }
}

package gay.pizza.foundation.core.features.gameplay

import gay.pizza.foundation.core.abstraction.Feature
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack
import org.koin.core.component.inject
import org.koin.dsl.module

class GameplayFeature : Feature() {
  private val config by inject<GameplayConfig>()

  override fun module() = module {
    single {
      plugin.loadConfigurationWithDefault(
        plugin,
        GameplayConfig.serializer(),
        "gameplay.yaml"
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

  @EventHandler(priority = EventPriority.HIGHEST)
  private fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
    val mainHandItem = event.player.inventory.itemInMainHand
    val hasLead = mainHandItem.type == Material.LEAD
    val livingEntity = event.rightClicked as? LivingEntity

    // If leads are allowed on all mobs, then start leading the mob.
    if (config.mobs.allowLeads && hasLead && livingEntity != null) {
      // Something to do with Bukkit, leashes must happen after the event.
      Bukkit.getScheduler().runTask(plugin) { ->
        // If the entity is already leashed, don't do anything.
        if (livingEntity.isLeashed) return@runTask

        // Interacted with the entity, don't despawn it.
        livingEntity.removeWhenFarAway = false

        val leashSuccess = livingEntity.setLeashHolder(event.player)

        if (leashSuccess) {
          val newStack = if (mainHandItem.amount == 1) {
            null
          } else {
            ItemStack(mainHandItem.type, mainHandItem.amount - 1)
          }
          event.player.inventory.setItemInMainHand(newStack)
        }
      }

      event.isCancelled = true
      return
    }
  }
}

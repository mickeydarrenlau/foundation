package gay.pizza.foundation.core.abstraction

import gay.pizza.foundation.core.FoundationCorePlugin
import org.bukkit.event.Listener
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module
import org.quartz.Scheduler

abstract class Feature : CoreFeature, KoinComponent, Listener {
  protected val plugin by inject<FoundationCorePlugin>()
  protected val scheduler by inject<Scheduler>()

  override fun enable() {}
  override fun disable() {}
  override fun module() = module {}
}

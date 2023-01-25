package gay.pizza.foundation.core.features.scheduler

import gay.pizza.foundation.core.abstraction.CoreFeature
import org.koin.dsl.module
import org.quartz.Scheduler
import org.quartz.impl.StdSchedulerFactory

class SchedulerFeature : CoreFeature {
  private val scheduler: Scheduler = StdSchedulerFactory.getDefaultScheduler()

  override fun enable() {
    scheduler.start()
  }

  override fun disable() {
    scheduler.shutdown(true)
  }

  override fun module() = module {
    single { scheduler }
  }
}

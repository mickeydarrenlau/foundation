package gay.pizza.foundation.core.features.scheduler

import org.quartz.Job
import org.quartz.JobExecutionContext

class SchedulerRunner : Job {
  override fun execute(context: JobExecutionContext) {
    @Suppress("UNCHECKED_CAST")
    val function = context.jobDetail.jobDataMap["function"] as () -> Unit
    function()
  }
}
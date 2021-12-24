package cloud.kubelet.foundation.core.features.scheduler

import org.quartz.Job
import org.quartz.JobExecutionContext

class SchedulerRunner : Job {
  override fun execute(context: JobExecutionContext) {
    val f = context.jobDetail.jobDataMap["function"] as () -> Unit
    f()
  }
}
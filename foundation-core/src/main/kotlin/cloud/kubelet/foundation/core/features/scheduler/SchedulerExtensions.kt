package cloud.kubelet.foundation.core.features.scheduler

import org.quartz.CronScheduleBuilder.cronSchedule
import org.quartz.JobBuilder.newJob
import org.quartz.JobDataMap
import org.quartz.Scheduler
import org.quartz.TriggerBuilder.newTrigger
import org.quartz.TriggerKey.triggerKey
import java.util.UUID

fun Scheduler.cron(cronExpression: String, f: () -> Unit): String {
  val id = UUID.randomUUID().toString()
  val job = newJob(SchedulerRunner::class.java).apply {
    setJobData(JobDataMap().apply {
      set("function", f)
    })
  }.build()

  val trigger = newTrigger()
    .withIdentity(triggerKey(id))
    .withSchedule(cronSchedule(cronExpression))
    .build()

  scheduleJob(job, trigger)
  return id
}

fun Scheduler.cancel(id: String) {
  unscheduleJob(triggerKey(id))
}

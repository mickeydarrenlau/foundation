package gay.pizza.foundation.core.features.backup

import kotlinx.serialization.Serializable

@Serializable
data class BackupConfig(
  val schedule: ScheduleConfig = ScheduleConfig(),
  val ignore: List<String> = listOf(
    "plugins/dynmap/web/**"
  ),
  val s3: S3Config = S3Config(),
)

@Serializable
data class ScheduleConfig(
  val cron: String = "",
)

@Serializable
data class S3Config(
  val accessKeyId: String = "",
  val secretAccessKey: String = "",
  val region: String = "",
  val endpointOverride: String = "",
  val bucket: String = "",
  val baseDirectory: String = "",
)

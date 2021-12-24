package cloud.kubelet.foundation.core.features.backup

import kotlinx.serialization.Serializable

@Serializable
data class BackupConfig(
  val s3: S3Config = S3Config(),
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

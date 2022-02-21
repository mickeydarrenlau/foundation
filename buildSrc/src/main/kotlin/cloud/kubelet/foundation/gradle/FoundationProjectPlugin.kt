package cloud.kubelet.foundation.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class FoundationProjectPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val versionWithBuild = if (System.getenv("CI_PIPELINE_IID") != null) {
      project.rootProject.version.toString() + ".${System.getenv("CI_PIPELINE_IID")}"
    } else {
      "DEV"
    }

    project.version = versionWithBuild
  }
}

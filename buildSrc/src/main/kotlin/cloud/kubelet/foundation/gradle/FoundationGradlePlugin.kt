package cloud.kubelet.foundation.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class FoundationGradlePlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.tasks.create("setupPaperServer", SetupPaperServer::class.java)
  }
}

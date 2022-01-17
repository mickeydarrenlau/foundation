package cloud.kubelet.foundation.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

class FoundationGradlePlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.extensions.create<FoundationExtension>("foundation")
    val setupPaperServer = project.tasks.create<SetupPaperServer>("setupPaperServer")
    project.afterEvaluate { ->
      setupPaperServer.dependsOn(*project.subprojects
        .filter { it.name.startsWith("foundation-") }
        .map { it.tasks.getByName("shadowJar") }
        .toTypedArray()
      )
    }
    val runPaperServer = project.tasks.create<RunPaperServer>("runPaperServer")
    runPaperServer.dependsOn(setupPaperServer)
  }
}

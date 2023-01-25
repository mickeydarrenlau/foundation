package gay.pizza.foundation.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

class FoundationGradlePlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.extensions.create<gay.pizza.foundation.gradle.FoundationExtension>("foundation")
    val setupPaperServer = project.tasks.create<gay.pizza.foundation.gradle.SetupPaperServer>("setupPaperServer")
    project.afterEvaluate { ->
      setupPaperServer.dependsOn(*project.subprojects
        .filter { it.name.startsWith("foundation-") }
        .map { it.tasks.getByName("shadowJar") }
        .toTypedArray()
      )
    }
    val runPaperServer = project.tasks.create<gay.pizza.foundation.gradle.RunPaperServer>("runPaperServer")
    runPaperServer.dependsOn(setupPaperServer)

    val updateManifests = project.tasks.create<gay.pizza.foundation.gradle.UpdateManifestTask>("updateManifests")
    project.tasks.getByName("assemble").dependsOn(updateManifests)
  }
}

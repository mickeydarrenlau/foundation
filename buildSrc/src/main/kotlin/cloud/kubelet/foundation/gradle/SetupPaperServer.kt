package cloud.kubelet.foundation.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files

open class SetupPaperServer : DefaultTask() {
  @get:Input
  lateinit var paperVersionGroup: String

  @get:Input
  lateinit var minecraftServerPath: String

  @TaskAction
  fun downloadPaperTask() {
    val minecraftServerDirectory = project.file(minecraftServerPath)
    val client = PaperVersionClient()
    val builds = client.getVersionBuilds(paperVersionGroup)
    val build = builds.last()
    val download = build.downloads["application"]!!
    val url = client.resolveDownloadUrl(build, download)

    if (!minecraftServerDirectory.exists()) {
      minecraftServerDirectory.mkdirs()
    }

    ant.invokeMethod(
      "get", mapOf(
        "src" to url.toString(),
        "dest" to project.file("${minecraftServerPath}/paper.jar").absolutePath
      )
    )

    val paperPluginsDirectory = minecraftServerDirectory.resolve("plugins")

    if (!paperPluginsDirectory.exists()) {
      paperPluginsDirectory.mkdirs()
    }

    for (project in project.subprojects) {
      if (!project.name.startsWith("foundation-")) {
        continue
      }

      val pluginJarFile = project.buildDir.resolve("libs/${project.name}-DEV-plugin.jar")
      val pluginLinkFile = paperPluginsDirectory.resolve("${project.name}.jar")
      if (pluginLinkFile.exists()) {
        pluginLinkFile.delete()
      }

      Files.createSymbolicLink(pluginLinkFile.toPath(), pluginJarFile.toPath())
    }
  }
}

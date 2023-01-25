package gay.pizza.foundation.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.kotlin.dsl.getByType
import java.io.File
import java.nio.file.Files

open class SetupPaperServer : DefaultTask() {
  init {
    outputs.upToDateWhen { false }
  }

  @get:Input
  @set:Option(option = "update", description = "Update Paper Server")
  var shouldUpdatePaperServer = false

  private val paperVersionClient = PaperVersionClient()

  @TaskAction
  fun downloadPaperTask() {
    val foundation = project.extensions.getByType<gay.pizza.foundation.gradle.FoundationExtension>()
    val minecraftServerDirectory = project.file(foundation.minecraftServerPath.get())

    if (!minecraftServerDirectory.exists()) {
      minecraftServerDirectory.mkdirs()
    }

    val paperJarFile = project.file("${foundation.minecraftServerPath.get()}/paper.jar")
    if (!paperJarFile.exists() || shouldUpdatePaperServer) {
      downloadLatestBuild(foundation.paperVersionGroup.get(), paperJarFile)
    }

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

  private fun downloadLatestBuild(paperVersionGroup: String, paperJarFile: File) {
    val builds = paperVersionClient.getVersionBuilds(paperVersionGroup)
    val build = builds.last()
    val download = build.downloads["application"]!!
    val url = paperVersionClient.resolveDownloadUrl(build, download)
    val downloader = SmartDownloader(paperJarFile.toPath(), url, download.sha256)
    if (downloader.download()) {
      logger.lifecycle("Installed Paper Server ${build.version} build ${build.build}")
    } else {
      logger.lifecycle("Paper Server ${build.version} build ${build.build} is up-to-date")
    }
  }
}

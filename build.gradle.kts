import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.com.google.gson.Gson
import java.io.FileWriter

plugins {
  java
  id("org.jetbrains.kotlin.jvm") version "1.6.10" apply false
  id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10" apply false
  id("com.github.johnrengelman.shadow") version "7.1.1" apply false
}

// Disable the JAR task for the root project.
tasks["jar"].enabled = false

allprojects {
  repositories {
    mavenCentral()
    maven {
      name = "papermc-repo"
      url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
      name = "sonatype"
      url = uri("https://oss.sonatype.org/content/groups/public/")
    }
  }
}

val manifestsDir = buildDir.resolve("manifests")
manifestsDir.mkdirs()
val gson = Gson()

tasks.create("updateManifests") {
  // TODO: not using task dependencies, outputs, blah blah blah.
  doLast {
    val updateFile = manifestsDir.resolve("update.json")
    val writer = FileWriter(updateFile)
    writer.use {
      val rootPath = rootProject.rootDir.toPath()
      val updateManifest = subprojects.mapNotNull { project ->
        val files = project.tasks.getByName("shadowJar").outputs
        val paths = files.files.map { rootPath.relativize(it.toPath()).toString() }

        if (paths.isNotEmpty()) project.name to mapOf(
          "version" to project.version,
          "artifacts" to paths,
        )
        else null
      }.toMap()

      gson.toJson(
        updateManifest,
        writer
      )
    }
  }
}

tasks.assemble {
  dependsOn("updateManifests")
}

subprojects {
  plugins.apply("org.jetbrains.kotlin.jvm")
  plugins.apply("org.jetbrains.kotlin.plugin.serialization")
  plugins.apply("com.github.johnrengelman.shadow")

  version = "0.2"
  group = "io.gorence"

  // Add build number if running under CI.
  val versionWithBuild = if (System.getenv("CI_PIPELINE_IID") != null) {
    version as String + ".${System.getenv("CI_PIPELINE_IID")}"
  } else {
    "DEV"
  }
  version = versionWithBuild

  dependencies {
    // Kotlin dependencies
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Serialization
    implementation("com.charleskorn.kaml:kaml:0.38.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")

    // Persistence
    implementation("org.jetbrains.xodus:xodus-openAPI:1.3.232")
    implementation("org.jetbrains.xodus:xodus-entity-store:1.3.232")

    // Paper API
    compileOnly("io.papermc.paper:paper-api:1.18.1-R0.1-SNAPSHOT")
  }

  java {
    val javaVersion = JavaVersion.toVersion(17)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
  }

  tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
      expand(props)
    }
  }

  tasks.withType<ShadowJar> {
    archiveClassifier.set("plugin")
  }

  tasks.assemble {
    dependsOn("shadowJar")
  }
}

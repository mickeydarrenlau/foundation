import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

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

subprojects {
  plugins.apply("org.jetbrains.kotlin.jvm")
  plugins.apply("org.jetbrains.kotlin.plugin.serialization")
  plugins.apply("com.github.johnrengelman.shadow")

  version = "0.1"
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
}

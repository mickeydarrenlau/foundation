import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  java
  id("gay.pizza.foundation.concrete-root") version "0.9.0"
  id("gay.pizza.foundation.concrete-library") version "0.9.0" apply false
  id("gay.pizza.foundation.concrete-plugin") version "0.9.0" apply false

  id("com.github.ben-manes.versions") version "0.45.0"
}

allprojects {
  repositories {
    mavenCentral()

    maven {
      name = "sonatype"
      url = uri("https://oss.sonatype.org/content/groups/public/")
    }
  }
}

version = "0.2"

subprojects {
  group = "gay.pizza.foundation"

  tasks.withType<KotlinCompile> {
    kotlinOptions {
      freeCompilerArgs += "-opt-in=kotlinx.serialization.ExperimentalSerializationApi"
    }
  }
}

concrete {
  minecraftServerPath.set("server")
  paperServerVersionGroup.set("1.18")
  paperApiVersion.set("1.18.2-R0.1-SNAPSHOT")
  acceptServerEula.set(true)
}

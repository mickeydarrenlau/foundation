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
  plugins.apply("org.jetbrains.kotlin.jvm")
  plugins.apply("org.jetbrains.kotlin.plugin.serialization")

  group = "gay.pizza.foundation"

  dependencies {
    // Kotlin dependencies
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Core libraries.
    implementation("io.insert-koin:koin-core:3.3.2")
    testImplementation("io.insert-koin:koin-test:3.3.2")

    // Serialization
    implementation("com.charleskorn.kaml:kaml:0.51.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")

    // Persistence
    implementation("org.jetbrains.xodus:xodus-openAPI:2.0.1")
    implementation("org.jetbrains.xodus:xodus-entity-store:2.0.1")
  }

  java {
    val javaVersion = JavaVersion.toVersion(17)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
  }

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

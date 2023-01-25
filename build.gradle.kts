import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import gay.pizza.foundation.gradle.FoundationProjectPlugin
import gay.pizza.foundation.gradle.isFoundationPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  java
  id("gay.pizza.foundation.gradle")
}

allprojects {
  repositories {
    mavenCentral()
    maven {
      name = "papermc"
      url = uri("https://papermc.io/repo/repository/maven-public/")
    }

    maven {
      name = "sonatype"
      url = uri("https://oss.sonatype.org/content/groups/public/")
    }
  }
}

tasks.assemble {
  dependsOn("updateManifests")
}

version = "0.2"

subprojects {
  plugins.apply("org.jetbrains.kotlin.jvm")
  plugins.apply("org.jetbrains.kotlin.plugin.serialization")
  plugins.apply("com.github.johnrengelman.shadow")
  plugins.apply(FoundationProjectPlugin::class)

  group = "lgbt.mystic"

  dependencies {
    // Kotlin dependencies
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Core libraries.
    implementation("io.insert-koin:koin-core:3.1.4")
    testImplementation("io.insert-koin:koin-test:3.1.4")

    // Serialization
    implementation("com.charleskorn.kaml:kaml:0.38.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")

    // Persistence
    implementation("org.jetbrains.xodus:xodus-openAPI:1.3.232")
    implementation("org.jetbrains.xodus:xodus-entity-store:1.3.232")

    // Paper API
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
  }

  java {
    val javaVersion = JavaVersion.toVersion(17)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
  }

  tasks.withType<KotlinCompile> {
    kotlinOptions {
      freeCompilerArgs =
        freeCompilerArgs + "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi"
    }
  }

  tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
      expand(props)
    }
  }

  if (project.isFoundationPlugin()) {
    tasks.withType<ShadowJar> {
      archiveClassifier.set("plugin")
    }
  }

  tasks.assemble {
    dependsOn("shadowJar")
  }
}

foundation {
  minecraftServerPath.set("server")
  paperVersionGroup.set("1.18")
}

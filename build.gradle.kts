plugins {
  kotlin("jvm") version "1.6.10"
  id("com.github.johnrengelman.shadow") version "7.1.1"
}

group = "io.gorence"
version = "1.0-SNAPSHOT"

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

dependencies {
  // Kotlin dependencies
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  // Database layer
  implementation("org.jetbrains.xodus:xodus-openAPI:1.3.232")

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

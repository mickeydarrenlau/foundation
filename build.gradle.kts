plugins {
  java
  id("com.github.johnrengelman.shadow") version("7.1.1")
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
  compileOnly("io.papermc.paper:paper-api:1.18.1-R0.1-SNAPSHOT")
  implementation("org.jetbrains.xodus:xodus-openAPI:1.3.232")
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

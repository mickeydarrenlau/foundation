plugins {
  `kotlin-dsl`
  kotlin("plugin.serialization") version "1.6.21"
}

repositories {
  gradlePluginPortal()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
  implementation("org.jetbrains.kotlin:kotlin-serialization:1.6.21")
  implementation("gradle.plugin.com.github.johnrengelman:shadow:7.1.2")
  implementation("com.google.code.gson:gson:2.9.0")
  implementation("org.bouncycastle:bcprov-jdk15on:1.70")
}

java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

gradlePlugin {
  plugins {
    create("foundation") {
      id = "cloud.kubelet.foundation.gradle"
      implementationClass = "cloud.kubelet.foundation.gradle.FoundationGradlePlugin"
    }
  }
}

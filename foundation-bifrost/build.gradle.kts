plugins {
  id("gay.pizza.foundation.concrete-plugin") version "0.7.0"
}

dependencies {
  implementation("net.dv8tion:JDA:5.0.0-alpha.2") {
    exclude(module = "opus-java")
  }

  compileOnly(project(":foundation-core"))
}

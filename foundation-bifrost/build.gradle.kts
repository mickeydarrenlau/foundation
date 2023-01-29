plugins {
  id("gay.pizza.foundation.concrete-plugin")
}

dependencies {
  implementation("net.dv8tion:JDA:5.0.0-beta.3") {
    exclude(module = "opus-java")
  }

  compileOnly(project(":foundation-core"))
}

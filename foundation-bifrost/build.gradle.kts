plugins {
  id("gay.pizza.foundation.concrete-plugin")
}

dependencies {
  implementation("net.dv8tion:JDA:5.0.0-alpha.2") {
    exclude(module = "opus-java")
  }

  implementation(project(":common-plugin"))
  compileOnly(project(":foundation-shared"))
}

plugins {
  id("gay.pizza.foundation.concrete-plugin")
}

dependencies {
  implementation(project(":common-plugin"))
  compileOnly(project(":foundation-shared"))
  implementation(project(":common-heimdall"))
}

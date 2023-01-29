plugins {
  id("gay.pizza.foundation.concrete-plugin")
}

dependencies {
  api(project(":common-heimdall"))
  compileOnly(project(":foundation-core"))
}

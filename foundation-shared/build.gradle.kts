plugins {
  id("gay.pizza.foundation.concrete-library")
}

dependencies {
  api(project(":common-all"))

  api("org.jetbrains.xodus:xodus-openAPI:2.0.1")
  api("org.jetbrains.xodus:xodus-entity-store:2.0.1")
}

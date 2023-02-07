plugins {
  id("gay.pizza.foundation.concrete-library")
}

dependencies {
  api(project(":common-all"))
  api(libs.xodus.core)
  api(libs.xodus.entity.store)
}

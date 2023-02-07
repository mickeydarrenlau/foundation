plugins {
  id("gay.pizza.foundation.concrete-library")
}

dependencies {
  api(project(":common-all"))
  compileOnly(project(":foundation-shared"))
}
